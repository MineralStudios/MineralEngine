package gg.mineral.server.network.connection

import gg.mineral.api.entity.living.human.Player
import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.Packet
import gg.mineral.api.network.packet.registry.PacketRegistry
import gg.mineral.api.network.packet.rw.ByteWriter
import gg.mineral.server.network.channel.FakeChannelImpl
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
import gg.mineral.server.snapshot.AsyncServerSnapshotImpl
import gg.mineral.server.util.datatypes.UUIDUtil
import gg.mineral.server.util.json.JsonUtil
import gg.mineral.server.util.login.LoginUtil
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.md_5.bungee.api.chat.BaseComponent
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.core.config.Configurator
import java.util.*
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class ConnectionImpl(override val serverSnapshot: AsyncServerSnapshotImpl) :
    SimpleChannelInboundHandler<Packet.Incoming>(),
    Connection,
    ByteWriter {
    var protocolState: PacketRegistry<Packet.Incoming> = ProtocolState.HANDSHAKE
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
        get() = serverSnapshot.playerConnections[this]
    override val ipAddress: String
        get() = channel?.remoteAddress().toString()
    private var connected = true

    fun attemptLogin(name: String) {
        this.name = name

        val player = serverSnapshot.playerNames[name]

        if (player != null) {
            disconnect(serverSnapshot.server.config.disconnectAlreadyLoggedIn)
            return
        }

        if (serverSnapshot.server.config.onlineMode && channel !is FakeChannelImpl) {
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
        this.lastKeepAlive = serverSnapshot.millis
        this.protocolState = ProtocolState.PLAY
    }

    fun spawnPlayer() =
        serverSnapshot.createPlayer(this).onJoin()

    fun sendPacket(vararg packets: Packet.Outgoing) {
        for (packet in packets) {
            channel!!.write(packet)
            LOGGER.debug("Packet sent: " + packet.javaClass.simpleName)
        }

        channel?.flush()
    }

    override fun queuePacket(vararg packets: Packet.Outgoing) {
        for (packet in packets) {
            channel!!.write(packet)
            packetsQueued = true
            LOGGER.debug("Packet queued: " + packet.javaClass.simpleName)
        }
    }

    override fun disconnect(vararg components: BaseComponent) {
        queuePacket(
            if (protocolState === ProtocolState.LOGIN)
                LoginDisconnectPacket(components)
            else
                DisconnectPacket(components)
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

        val url = "https://sessionserver.mojang.com/session/minecraft/hasJoined?username=$name&serverId=$serverId"

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
        runBlocking {
            serverSnapshot.disconnected(this@ConnectionImpl)
        }
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
    override fun channelRead0(ctx: ChannelHandlerContext, received: Packet.Incoming) {
        LOGGER.debug("Packet received: " + received.javaClass.simpleName)

        if (received is Packet.SyncHandler) {
            serverSnapshot.execute {
                runBlocking {
                    received.receivedSync(
                        this@ConnectionImpl
                    )
                }
            }
        }

        if (received is Packet.EventLoopHandler) runBlocking { received.receivedEventLoop(this@ConnectionImpl) }

        if (received is Packet.AsyncHandler) serverSnapshot.scope.launch {
            received.receivedAsync(this@ConnectionImpl)
        }
    }

    override fun call(): Void? {
        if (protocolState === ProtocolState.PLAY && serverSnapshot.millis - lastKeepAlive > 17500) { // 17.5
            // seconds
            // for
            // timeout
            queuePacket(KeepAlivePacket(0))
            lastKeepAlive = serverSnapshot.millis
        }

        if (packetsQueued) {
            channel?.flush()
            packetsQueued = false
        }

        if (!connected) serverSnapshot.connections.remove(this)
        return null
    }

    companion object {
        private val LOGGER: Logger = LogManager.getLogger(Connection::class.java)

        init {
            Configurator.setAllLevels(LOGGER.name, Level.getLevel("debug"))
        }
    }
}
