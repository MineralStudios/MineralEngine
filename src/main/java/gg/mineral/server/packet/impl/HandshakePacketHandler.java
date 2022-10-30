package gg.mineral.server.packet.impl;

import dev.zerite.craftlib.protocol.Packet;
import dev.zerite.craftlib.protocol.connection.NettyConnection;
import dev.zerite.craftlib.protocol.packet.handshake.client.ClientHandshakePacket;
import gg.mineral.server.packet.IHandshakePacketHandler;

public class HandshakePacketHandler implements IHandshakePacketHandler {

    @Override
    public void handle(NettyConnection connection, ClientHandshakePacket packet) {
        connection.version = packet.getVersion();
        connection.state = packet.getNextState();
    }

    @Override
    public void handle(NettyConnection connection, Packet packet) {
        handle(connection, (ClientHandshakePacket) packet);
    }

}
