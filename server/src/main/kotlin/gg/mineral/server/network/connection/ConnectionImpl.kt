package gg.mineral.server.network.connection

import dev.zerite.craftlib.chat.component.BaseChatComponent
import gg.mineral.api.entity.living.human.Player
import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.Packet
import gg.mineral.api.network.packet.registry.PacketRegistry
import gg.mineral.api.network.packet.rw.ByteWriter
import gg.mineral.server.MinecraftServerImpl
import gg.mineral.server.network.login.LoginAuthData
import gg.mineral.server.network.packet.handler.PacketDecrypter
import gg.mineral.server.network.packet.handler.PacketEncrypter
import gg.mineral.server.network.packet.login.clientbound.EncryptionRequestPacket
import gg.mineral.server.network.packet.login.clientbound.LoginDisconnectPacket
import gg.mineral.server.network.packet.login.clientbound.LoginSuccessPacket
import gg.mineral.server.network.packet.play.bidirectional.KeepAlivePacket
import gg.mineral.server.network.packet.play.clientbound.DisconnectPacket
import gg.mineral.server.network.protocol.ProtocolState
import gg.mineral.server.network.protocol.ProtocolVersion
import gg.mineral.server.util.datatypes.UUIDUtil
import gg.mineral.server.util.json.JsonUtil
import gg.mineral.server.util.login.LoginUtil
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.core.config.Configurator
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class ConnectionImpl(override val server: MinecraftServerImpl) : SimpleChannelInboundHandler<Packet.INCOMING>(),
    Connection,
    ByteWriter {
    private val packetQueue = ConcurrentLinkedQueue<Runnable>()
    var protocolState: PacketRegistry<Packet.INCOMING> = ProtocolState.HANDSHAKE
        set(value) {
            field = value
            channel!!.attr(ProtocolState.ATTRIBUTE_KEY).set(protocolState)
            if (value == ProtocolState.PLAY)
                this.loginAuthData = null
        }
    var protocolVersion = ProtocolVersion.V1_7_6
    private var channel: Channel? = null
    var lastKeepAlive: Long = 0
    override var ping = 0
    private var loginAuthData: LoginAuthData? = null
    override var name: String? = null
    override var uuid: UUID? = null
    private var packetsQueued = false
    override val player: Player?
        get() = server.playerConnections[this]
    override val ipAddress: String
        get() = channel?.remoteAddress().toString()
    private var connected = true

    fun attemptLogin(name: String) {
        this.name = name

        val player = server.playerNames[name]

        if (player != null) {
            disconnect(server.config.disconnectAlreadyLoggedIn)
            return
        }

        if (server.config.onlineMode) {
            this.loginAuthData = LoginAuthData()
            queuePacket(
                EncryptionRequestPacket(
                    "",
                    loginAuthData!!.keyPair.public, loginAuthData!!.verifyToken
                )
            )
            return
        }

        this.uuid = UUIDUtil.fromName(name)
        this.loginSuccess()
        this.spawnPlayer()
    }

    fun loginSuccess() {
        this.queuePacket(LoginSuccessPacket(uuid!!, name!!))
        this.lastKeepAlive = server.getMillis()
        this.protocolState = ProtocolState.PLAY
    }

    fun spawnPlayer() =
        server.createPlayer(this).onJoin()

    fun sendPacket(vararg packets: Packet.OUTGOING) {
        for (packet in packets) {
            channel!!.write(packet)
            LOGGER.debug("Packet sent: " + packet.javaClass.simpleName)
        }

        channel?.flush()
    }

    override fun queuePacket(vararg packets: Packet.OUTGOING) {
        for (packet in packets) {
            channel!!.write(packet)
            packetsQueued = true
            LOGGER.debug("Packet queued: " + packet.javaClass.simpleName)
        }
    }

    override fun disconnect(disconnectMessage: BaseChatComponent) {
        queuePacket(
            if (protocolState === ProtocolState.LOGIN)
                LoginDisconnectPacket(disconnectMessage)
            else
                DisconnectPacket(disconnectMessage)
        )
        close()
    }

    fun authenticate(encryptedSharedSecret: ByteArray, encryptedVerifyToken: ByteArray): SecretKey? {
        if (!loginAuthData!!.verifyToken.contentEquals(
                LoginUtil.decryptRsa(
                    loginAuthData!!.keyPair,
                    encryptedVerifyToken
                )
            )
        ) return null

        val decryptedSharedSecret = LoginUtil.decryptRsa(
            loginAuthData!!.keyPair,
            encryptedSharedSecret
        )

        val serverId = LoginUtil.hashSharedSecret(
            loginAuthData!!.keyPair.public,
            decryptedSharedSecret
        )

        val url = ("https://sessionserver.mojang.com/session/minecraft/hasJoined?username=" +
                this.name
                + "&serverId="
                + serverId)

        val json = JsonUtil.getJsonObject(url)

        val id = json.getString("id") ?: return null

        val uuid = UUIDUtil.fromString(id)

        this.uuid = uuid

        return SecretKeySpec(decryptedSharedSecret, "AES")
    }

    @Throws(Exception::class)
    override fun channelActive(channelhandlercontext: ChannelHandlerContext) {
        super.channelActive(channelhandlercontext)
        this.channel = channelhandlercontext.channel()
        this.connected = true
        this.protocolState = ProtocolState.HANDSHAKE
    }

    @Throws(Exception::class)
    override fun channelInactive(ctx: ChannelHandlerContext) {
        server.disconnected(this)
        this.protocolState = ProtocolState.HANDSHAKE
        this.connected = false
        super.channelInactive(ctx)
    }

    private fun close() {
        channel?.close()
    }

    fun enableEncryption(secretkey: SecretKey) {
        channel!!.pipeline().addBefore(
            "decoder", "decrypt",
            PacketDecrypter(secretkey)
        ).addBefore("prepender", "encrypt", PacketEncrypter(secretkey))
    }

    @Throws(Exception::class)
    override fun channelRead0(ctx: ChannelHandlerContext, received: Packet.INCOMING) {
        LOGGER.debug("Packet received: " + received.javaClass.simpleName)
        if (protocolState === ProtocolState.PLAY || protocolState === ProtocolState.LOGIN) packetQueue.add(Runnable {
            received.received(
                this
            )
        })
        else received.received(this)

        if (received is Packet.ASYNC_INCOMING) server.asyncExecutor.submit {
            received.receivedAsync(this)
        }
    }

    override fun call(): Void? {
        while (!packetQueue.isEmpty()) packetQueue.poll().run()

        if (protocolState === ProtocolState.PLAY && server.getMillis() - lastKeepAlive > 17500) { // 17.5
            // seconds
            // for
            // timeout
            queuePacket(KeepAlivePacket(0))
            lastKeepAlive = server.getMillis()
        }

        if (packetsQueued) {
            channel?.flush()
            packetsQueued = false
        }

        if (!connected) server.connections.remove(this)
        return null
    }

    companion object {
        private val LOGGER: Logger = LogManager.getLogger(Connection::class.java)

        init {
            Configurator.setAllLevels(LOGGER.name, Level.getLevel("debug"))
        }
    }
}
