package gg.mineral.server.packet;

import dev.zerite.craftlib.protocol.Packet;
import dev.zerite.craftlib.protocol.connection.NettyConnection;
import dev.zerite.craftlib.protocol.packet.handshake.client.ClientHandshakePacket;

public interface IHandshakePacketHandler {
    public void handle(NettyConnection connection, Packet packet);

    public void handle(NettyConnection connection, ClientHandshakePacket packet);
}
