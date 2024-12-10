package gg.mineral.server.network.connection

import dev.zerite.craftlib.chat.component.BaseChatComponent
import gg.mineral.api.entity.living.human.Player
import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.Packet
import gg.mineral.api.network.packet.registry.PacketRegistry
import gg.mineral.api.network.packet.rw.ByteWriter
import gg.mineral.server.MinecraftServerImpl
import gg.mineral.server.network.login.LoginAuthData
import gg.mineral.server.network.packet.handler.EncryptionHandler
import gg.mineral.server.network.packet.login.clientbound.EncryptionRequestPacket
import gg.mineral.server.network.packet.login.clientbound.LoginDisconnectPacket
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
import lombok.Getter
import lombok.RequiredArgsConstructor
import lombok.Setter
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@RequiredArgsConstructor
class ConnectionImpl : SimpleChannelInboundHandler<Packet.INCOMING>(), Connection,
    ByteWriter {
    @Getter
    private val server: MinecraftServerImpl? = null
    private val packetQueue: Queue<Runnable> = ConcurrentLinkedQueue()

    @Getter
    private var protocolState: PacketRegistry<Packet.INCOMING> = ProtocolState.HANDSHAKE

    @Setter
    @Getter
    private val protocolVersion = ProtocolVersion.V1_7_6
    private var channel: Channel? = null
    private var lastKeepAlive = System.currentTimeMillis()
    private var loginAuthData: LoginAuthData? = null

    @Getter
    private var name: String? = null

    @Getter
    @Setter
    private var uuid: UUID? = null
    private var packetsQueued = false

    @Getter
    private var connected = true

    fun attemptLogin(name: String?) {
        this.name = name

        val player = server!!.playerNames[name]

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
        this.loggedIn()
    }

    @Throws(IllegalStateException::class)
    fun loggedIn() {
        this.loginAuthData = null
        this.setProtocolState(ProtocolState.PLAY)
        server!!.createPlayer(this).onJoin()
    }

    fun sendPacket(vararg packets: Packet.OUTGOING) {
        for (packet in packets) {
            channel!!.write(serialize(packet))
            LOGGER.debug("Packet sent: " + packet.javaClass.simpleName)
        }

        channel!!.flush()
    }

    override fun queuePacket(vararg packets: Packet.OUTGOING) {
        for (packet in packets) {
            channel!!.write(serialize(packet))
            packetsQueued = true
            LOGGER.debug("Packet queued: " + packet.javaClass.simpleName)
        }
    }

    override fun disconnect(chatComponent: BaseChatComponent) {
        queuePacket(
            if (getProtocolState() === ProtocolState.LOGIN)
                LoginDisconnectPacket(chatComponent)
            else
                DisconnectPacket(chatComponent)
        )
        close()
    }

    fun authenticate(encryptedSharedSecret: ByteArray?, encryptedVerifyToken: ByteArray?): Boolean {
        if (!loginAuthData!!.verifyToken.contentEquals(
                LoginUtil.decryptRsa(
                    loginAuthData!!.keyPair,
                    encryptedVerifyToken
                )
            )
        ) return false

        val decryptedSharedSecret = LoginUtil.decryptRsa(
            loginAuthData!!.keyPair,
            encryptedSharedSecret
        )

        val serverId = LoginUtil.hashSharedSecret(
            loginAuthData!!.keyPair.public,
            decryptedSharedSecret
        )

        val url = ("https://sessionserver.mojang.com/session/minecraft/hasJoined?username=" +
                this.getName()
                + "&serverId="
                + serverId)

        val json = JsonUtil.getJsonObject(url) ?: return false

        val id = json.getString("id") ?: return false

        val uuid = UUIDUtil.fromString(id)

        this.setUuid(uuid)

        val secretKey = SecretKeySpec(decryptedSharedSecret, "AES")

        enableEncryption(secretKey)

        return true
    }

    @Throws(Exception::class)
    override fun channelActive(channelhandlercontext: ChannelHandlerContext) {
        super.channelActive(channelhandlercontext)
        this.channel = channelhandlercontext.channel()
        this.connected = true
        setProtocolState(ProtocolState.HANDSHAKE)
    }

    @Throws(Exception::class)
    override fun channelInactive(ctx: ChannelHandlerContext) {
        server!!.disconnected(this)
        setProtocolState(ProtocolState.HANDSHAKE)
        this.connected = false
        super.channelInactive(ctx)
    }

    fun close() {
        channel!!.close()
    }

    fun enableEncryption(secretkey: SecretKey?) {
        channel!!.pipeline().addBefore(
            "decoder", "encryption",
            EncryptionHandler(secretkey)
        )
    }

    fun setProtocolState(protocolState: PacketRegistry<Packet.INCOMING>) {
        this.protocolState = protocolState
        channel!!.attr(ProtocolState.ATTRIBUTE_KEY).set(protocolState)
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

        if (received is Packet.ASYNC_INCOMING) server!!.asyncExecutor.submit {
            received.receivedAsync(this)
        }
    }

    override fun call(): Void? {
        while (!packetQueue.isEmpty()) packetQueue.poll().run()

        if (getProtocolState() === ProtocolState.PLAY && System.currentTimeMillis() - lastKeepAlive > 17500) { // 17.5
            // seconds
            // for
            // timeout
            queuePacket(KeepAlivePacket(0))
            lastKeepAlive = System.currentTimeMillis()
        }

        if (packetsQueued) {
            channel!!.flush()
            packetsQueued = false
        }

        if (!connected) server!!.connections.remove(this)
        return null
    }

    override fun getIpAddress(): String {
        return channel!!.remoteAddress().toString()
    }

    override fun getPlayer(): Player {
        return server!!.playerConnections[this]!!
    }

    companion object {
        private val LOGGER: Logger = LogManager.getLogger(Connection::class.java)
    }
}
