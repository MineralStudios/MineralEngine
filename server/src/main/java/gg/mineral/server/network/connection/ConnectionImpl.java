package gg.mineral.server.network.connection;

import java.util.Arrays;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import dev.zerite.craftlib.chat.component.BaseChatComponent;
import gg.mineral.api.entity.living.human.Player;
import gg.mineral.api.network.connection.Connection;
import gg.mineral.api.network.packet.Packet;
import gg.mineral.api.network.packet.Packet.INCOMING;
import gg.mineral.api.network.packet.registry.PacketRegistry;
import gg.mineral.api.network.packet.rw.ByteWriter;
import gg.mineral.server.MinecraftServerImpl;
import gg.mineral.server.network.login.LoginAuthData;
import gg.mineral.server.network.packet.handler.EncryptionHandler;
import gg.mineral.server.network.packet.login.clientbound.EncryptionRequestPacket;
import gg.mineral.server.network.packet.login.clientbound.LoginDisconnectPacket;
import gg.mineral.server.network.packet.play.bidirectional.KeepAlivePacket;
import gg.mineral.server.network.packet.play.clientbound.DisconnectPacket;
import gg.mineral.server.network.protocol.ProtocolState;
import gg.mineral.server.network.protocol.ProtocolVersion;
import gg.mineral.server.util.datatypes.UUIDUtil;
import gg.mineral.server.util.json.JsonUtil;
import gg.mineral.server.util.login.LoginUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.val;

@RequiredArgsConstructor
public class ConnectionImpl extends SimpleChannelInboundHandler<Packet.INCOMING> implements Connection, ByteWriter {
    private static final Logger LOGGER = LogManager.getLogger(Connection.class);
    @Getter
    private PacketRegistry<INCOMING> protocolState = ProtocolState.HANDSHAKE;
    @Setter
    @Getter
    private byte protocolVersion = ProtocolVersion.V1_7_6;
    private Channel channel;
    private long lastKeepAlive = System.currentTimeMillis();
    private LoginAuthData loginAuthData;
    @Getter
    private String name;
    @Getter
    @Setter
    @Nullable
    private UUID uuid;
    private boolean packetsQueued;
    @Getter
    private final MinecraftServerImpl server;
    private final Queue<Runnable> packetQueue = new ConcurrentLinkedQueue<>();
    @Getter
    private boolean connected = true;

    public void attemptLogin(String name) {
        this.name = name;

        val player = server.getPlayerNames().get(name);

        if (player != null) {
            disconnect(server.getConfig().getDisconnectAlreadyLoggedIn());
            return;
        }

        if (server.getConfig().isOnlineMode()) {
            this.loginAuthData = new LoginAuthData();
            queuePacket(new EncryptionRequestPacket("",
                    this.loginAuthData.getKeyPair().getPublic(), this.loginAuthData.getVerifyToken()));
            return;
        }

        this.uuid = UUIDUtil.fromName(name);
        this.loggedIn();
    }

    public void loggedIn() throws IllegalStateException {
        this.loginAuthData = null;
        this.setProtocolState(ProtocolState.PLAY);
        server.createPlayer(this).onJoin();
    }

    public void sendPacket(Packet.OUTGOING... packets) {
        for (val packet : packets) {
            channel.write(serialize(packet));
            LOGGER.debug("Packet sent: " + packet.getClass().getSimpleName());
        }

        channel.flush();
    }

    public void queuePacket(Packet.OUTGOING... packets) {
        for (val packet : packets) {
            channel.write(serialize(packet));
            packetsQueued = true;
            LOGGER.debug("Packet queued: " + packet.getClass().getSimpleName());
        }
    }

    public void disconnect(BaseChatComponent chatComponent) {
        queuePacket(
                getProtocolState() == ProtocolState.LOGIN ? new LoginDisconnectPacket(chatComponent)
                        : new DisconnectPacket(chatComponent));
        close();
    }

    public boolean authenticate(byte[] encryptedSharedSecret, byte[] encryptedVerifyToken) {
        if (!Arrays.equals(this.loginAuthData.getVerifyToken(),
                LoginUtil.decryptRsa(this.loginAuthData.getKeyPair(), encryptedVerifyToken)))
            return false;

        val decryptedSharedSecret = LoginUtil.decryptRsa(this.loginAuthData.getKeyPair(),
                encryptedSharedSecret);

        val serverId = LoginUtil.hashSharedSecret(this.loginAuthData.getKeyPair().getPublic(),
                decryptedSharedSecret);

        val url = "https://sessionserver.mojang.com/session/minecraft/hasJoined?username=" +
                this.getName()
                + "&serverId="
                + serverId;

        val json = JsonUtil.getJsonObject(url);

        if (json == null)
            return false;

        val id = json.getString("id");

        if (id == null)
            return false;

        val uuid = UUIDUtil.fromString(id);

        this.setUuid(uuid);

        val secretKey = new SecretKeySpec(decryptedSharedSecret, "AES");

        enableEncryption(secretKey);

        return true;
    }

    @Override
    public void channelActive(ChannelHandlerContext channelhandlercontext) throws Exception {
        super.channelActive(channelhandlercontext);
        this.channel = channelhandlercontext.channel();
        this.connected = true;
        setProtocolState(ProtocolState.HANDSHAKE);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        server.disconnected(this);
        setProtocolState(ProtocolState.HANDSHAKE);
        this.connected = false;
        super.channelInactive(ctx);
    }

    public void close() {
        channel.close();
    }

    public void enableEncryption(SecretKey secretkey) {
        this.channel.pipeline().addBefore("decoder", "encryption",
                new EncryptionHandler(secretkey));
    }

    public void setProtocolState(PacketRegistry<INCOMING> protocolState) {
        this.protocolState = protocolState;
        this.channel.attr(ProtocolState.ATTRIBUTE_KEY).set(protocolState);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet.INCOMING received) throws Exception {
        LOGGER.debug("Packet received: " + received.getClass().getSimpleName());
        if (protocolState == ProtocolState.PLAY || protocolState == ProtocolState.LOGIN)
            packetQueue.add(() -> received.received(this));
        else
            received.received(this);

        if (received instanceof Packet.ASYNC_INCOMING async)
            server.getAsyncExecutor().submit(() -> async.receivedAsync(this));
    }

    @Override
    public Void call() {
        while (!packetQueue.isEmpty())
            packetQueue.poll().run();

        if (getProtocolState() == ProtocolState.PLAY && System.currentTimeMillis() - lastKeepAlive > 17500) { // 17.5
                                                                                                              // seconds
                                                                                                              // for
                                                                                                              // timeout
            queuePacket(new KeepAlivePacket(0));
            lastKeepAlive = System.currentTimeMillis();
        }

        if (packetsQueued) {
            channel.flush();
            packetsQueued = false;
        }

        if (!connected)
            server.getConnections().remove(this);
        return null;
    }

    @Override
    public String getIpAddress() {
        return channel.remoteAddress().toString();
    }

    @Override
    public Player getPlayer() {
        return server.getPlayerConnections().get(this);
    }

}
