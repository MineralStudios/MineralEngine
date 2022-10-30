package gg.mineral.server;

import dev.zerite.craftlib.chat.component.BaseChatComponent;
import dev.zerite.craftlib.protocol.Packet;
import dev.zerite.craftlib.protocol.connection.NettyConnection;
import dev.zerite.craftlib.protocol.connection.PacketHandler;
import dev.zerite.craftlib.protocol.connection.PacketSendingEvent;
import gg.mineral.server.packet.impl.HandshakePacketHandler;
import gg.mineral.server.packet.impl.StatusPacketHandler;

public class ServerConnection implements PacketHandler {

    StatusPacketHandler statusPacketHandler = new StatusPacketHandler();
    HandshakePacketHandler handshakePacketHandler = new HandshakePacketHandler();

    @Override
    public void assigned(NettyConnection arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void connected(NettyConnection arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void disconnected(NettyConnection arg0, BaseChatComponent arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void exception(NettyConnection arg0, Throwable arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void received(NettyConnection arg0, Packet packet) {
        statusPacketHandler.handle(arg0, packet);
    }

    @Override
    public void sending(NettyConnection arg0, PacketSendingEvent arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void sent(NettyConnection arg0, Packet arg1) {
        // TODO Auto-generated method stub

    }

}
