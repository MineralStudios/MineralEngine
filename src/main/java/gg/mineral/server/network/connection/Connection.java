package gg.mineral.server.network.connection;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.jetbrains.annotations.Nullable;

import dev.zerite.craftlib.chat.component.BaseChatComponent;
import gg.mineral.server.MinecraftServer;
import gg.mineral.server.entity.living.human.Player;
import gg.mineral.server.entity.manager.EntityManager;
import gg.mineral.server.network.login.LoginAuthData;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.network.packet.handler.EncryptionHandler;
import gg.mineral.server.network.packet.login.clientbound.EncryptionRequestPacket;
import gg.mineral.server.network.packet.login.clientbound.LoginDisconnectPacket;
import gg.mineral.server.network.packet.play.bidirectional.KeepAlivePacket;
import gg.mineral.server.network.packet.play.clientbound.DisconnectPacket;
import gg.mineral.server.network.packet.registry.IncomingPacketRegistry;
import gg.mineral.server.network.protocol.ProtocolState;
import gg.mineral.server.network.protocol.ProtocolVersion;
import gg.mineral.server.util.collection.GlueList;
import gg.mineral.server.util.datatypes.UUIDUtil;
import gg.mineral.server.util.json.JsonUtil;
import gg.mineral.server.util.login.LoginUtil;
import gg.mineral.server.util.messages.Messages;
import gg.mineral.server.util.network.PacketUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.val;

@RequiredArgsConstructor
public class Connection extends SimpleChannelInboundHandler<Packet.INCOMING> {
    public static List<Connection> LIST = new GlueList<>();
    @Getter
    IncomingPacketRegistry protocolState = ProtocolState.HANDSHAKE;
    public byte PROTOCOL_VERSION = ProtocolVersion.V1_7_6;
    Channel channel;
    long lastKeepAlive = System.currentTimeMillis();
    LoginAuthData loginAuthData;
    @Getter
    String name;
    @Getter
    @Setter
    @Nullable
    UUID uuid;
    boolean packetsQueued;
    @Getter
    private final MinecraftServer server;

    public void attemptLogin(String name) {
        this.name = name;

        for (val entity : EntityManager.getEntities().values())
            if (entity instanceof Player player)
                if (player.getName().equalsIgnoreCase(name)) {
                    disconnect(Messages.DISCONNECT_ALREADY_LOGGED_IN);
                    return;
                }

        this.loginAuthData = new LoginAuthData();
        sendPacket(new EncryptionRequestPacket("",
                this.loginAuthData.getKeyPair().getPublic(), this.loginAuthData.getVerifyToken()));
    }

    public void loggedIn() {
        setProtocolState(ProtocolState.PLAY);
        EntityManager.create(this).onJoin();
    }

    public void sendPacket(Packet.OUTGOING... packets) {
        for (val packet : packets) {
            channel.write(PacketUtil.serialize(packet));

            if (server.debugMessages)
                System.out.println("[Mineral] Packet sent: " + packet.getClass().getSimpleName());
        }

        channel.flush();
    }

    public void queuePacket(Packet.OUTGOING... packets) {
        for (val packet : packets) {
            channel.write(PacketUtil.serialize(packet));
            packetsQueued = true;

            if (server.debugMessages)
                System.out.println("[Mineral] Packet queued: " + packet.getClass().getSimpleName());
        }
    }

    public void disconnect(BaseChatComponent chatComponent) {
        sendPacket(
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

        setProtocolState(ProtocolState.HANDSHAKE);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        EntityManager.remove(this);
        setProtocolState(ProtocolState.HANDSHAKE);
        LIST.remove(this);
        super.channelInactive(ctx);
    }

    public void close() {
        channel.close();
    }

    public void tick() {
        if (getProtocolState() == ProtocolState.PLAY && System.currentTimeMillis() - lastKeepAlive > 17500) {
            queuePacket(new KeepAlivePacket(0));
            lastKeepAlive = System.currentTimeMillis();
        }

        if (packetsQueued) {
            channel.flush();
            packetsQueued = false;
        }
    }

    public void enableEncryption(SecretKey secretkey) {
        this.channel.pipeline().addBefore("decoder", "encryption",
                new EncryptionHandler(secretkey));
    }

    public void setProtocolState(IncomingPacketRegistry protocolState) {
        this.protocolState = protocolState;
        this.channel.attr(ProtocolState.ATTRIBUTE_KEY).set(protocolState);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet.INCOMING received) throws Exception {
        if (server.debugMessages)
            System.out.println("[Mineral] Packet received: " + received.getClass().getSimpleName());
        received.received(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Connection) || obj == null)
            return false;

        Connection connection = (Connection) obj;

        if (connection.getUuid() == null || getUuid() == null)
            return false;

        return connection.getUuid().equals(getUuid());
    }

    @Override
    public int hashCode() {
        return getUuid().hashCode();
    }
}
