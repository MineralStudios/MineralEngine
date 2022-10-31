package gg.mineral.server;

import dev.zerite.craftlib.chat.component.BaseChatComponent;
import dev.zerite.craftlib.protocol.Packet;
import dev.zerite.craftlib.protocol.connection.NettyConnection;
import dev.zerite.craftlib.protocol.connection.PacketHandler;
import dev.zerite.craftlib.protocol.connection.PacketSendingEvent;
import dev.zerite.craftlib.protocol.version.MinecraftProtocol;
import dev.zerite.craftlib.protocol.version.ProtocolVersion;
import gg.mineral.server.entity.PlayerManager;
import gg.mineral.server.packet.impl.HandshakePacketHandler;
import gg.mineral.server.packet.impl.LoginPacketHandler;
import gg.mineral.server.packet.impl.PlayPacketHandler;
import gg.mineral.server.packet.impl.StatusPacketHandler;

public class ServerConnection implements PacketHandler {

    static StatusPacketHandler statusPacketHandler = new StatusPacketHandler();
    static PlayPacketHandler playPacketHandler = new PlayPacketHandler();
    static LoginPacketHandler loginPacketHandler = new LoginPacketHandler();
    static HandshakePacketHandler handshakePacketHandler = new HandshakePacketHandler();

    @Override
    public void assigned(NettyConnection connection) {
        // TODO Auto-generated method stub

    }

    @Override
    public void connected(NettyConnection connection) {
        connection.state = MinecraftProtocol.HANDSHAKE;
        connection.version = ProtocolVersion.MC1_7_6;
    }

    @Override
    public void disconnected(NettyConnection connection, BaseChatComponent arg1) {
        PlayerManager.remove(p -> p.getConnection().equals(connection));
    }

    @Override
    public void exception(NettyConnection connection, Throwable arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void received(NettyConnection connection, Packet packet) {

        if (connection.state == MinecraftProtocol.HANDSHAKE) {
            handshakePacketHandler.handle(connection, packet);
            return;
        }

        if (connection.state == MinecraftProtocol.LOGIN) {
            loginPacketHandler.handle(connection, packet);
            return;
        }

        if (connection.state == MinecraftProtocol.PLAY) {
            playPacketHandler.handle(connection, packet);
            return;
        }

        if (connection.state == MinecraftProtocol.STATUS) {
            statusPacketHandler.handle(connection, packet);
            return;
        }
    }

    @Override
    public void sending(NettyConnection connection, PacketSendingEvent arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void sent(NettyConnection connection, Packet arg1) {
        // TODO Auto-generated method stub

    }

}
