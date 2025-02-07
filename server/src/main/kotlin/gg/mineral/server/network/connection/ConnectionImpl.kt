package gg.mineral.server.network.connection

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
import io.netty.channel.local.LocalChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.md_5.bungee.api.chat.BaseComponent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.*
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class ConnectionImpl(override val server: MinecraftServerImpl) :
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
    var channel: Channel? = null
    var lastKeepAlive: Long = 0
    override var ping = 0
    private var loginAuthData: LoginAuthData? = null
    private val authMutex = Mutex()
    override var name: String? = null
    override var uuid: UUID? = null
    private var packetsQueued = false
    override val player: Player?
        get() = server.playerConnections[this]
    override val ipAddress: String
        get() = channel?.remoteAddress().toString()
    private var connected = true
    var clientSideActive = false

    fun attemptLogin(
        name: String,
        uuid: UUID = UUIDUtil.fromName(name),
        x: Double = 0.0,
        y: Double = 0.0,
        z: Double = 0.0,
        yaw: Float = 0.0f,
        pitch: Float = 0.0f
    ) {
        this.name = name

        val player = server.playerNames[name]

        if (player != null && channel !is LocalChannel) {
            disconnect(server.config.disconnectAlreadyLoggedIn)
            return
        }

        if (server.config.onlineMode && channel !is LocalChannel) {
            this.loginAuthData = LoginAuthData()
            queuePacket(
                EncryptionRequestPacket(
                    "",
                    loginAuthData!!.keyPair.public, loginAuthData!!.verifyToken
                )
            )
            return
        }

        this.uuid = uuid

        this.loginSuccess()
        this.spawnPlayer(x, y, z, yaw, pitch)
    }

    fun loginSuccess() {
        this.queuePacket(LoginSuccessPacket(uuid!!, name!!))
        this.lastKeepAlive = server.millis
        this.protocolState = ProtocolState.PLAY
    }

    fun spawnPlayer(
        x: Double = 0.0,
        y: Double = 0.0,
        z: Double = 0.0,
        yaw: Float = 0.0f,
        pitch: Float = 0.0f
    ) =
        server.createPlayer(this, x, y, z, yaw, pitch)

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

    suspend fun authenticate(encryptedSharedSecret: ByteArray, encryptedVerifyToken: ByteArray): SecretKey? =
        authMutex.withLock {
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
        server.disconnected(this@ConnectionImpl)
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

        if (received is Packet.ChannelWhitelist<*>)
            if (!received.kClass.isInstance(channel))
                return

        if (received is Packet.SyncHandler) {
            server.execute {
                received.receivedSync(
                    this@ConnectionImpl
                )
            }
        }

        if (received is Packet.EventLoopHandler) runBlocking { received.receivedEventLoop(this@ConnectionImpl) }

        if (received is Packet.AsyncHandler) server.asyncScope.launch {
            received.receivedAsync(this@ConnectionImpl)
        }
    }

    override fun call(): Void? {
        if (protocolState === ProtocolState.PLAY && server.millis - lastKeepAlive > 17500 && clientSideActive) { // 17.5
            // seconds
            // for
            // timeout
            queuePacket(KeepAlivePacket(0))
            lastKeepAlive = server.millis
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
            //Configurator.setAllLevels(LOGGER.name, Level.getLevel("debug"))
        }
    }
}
