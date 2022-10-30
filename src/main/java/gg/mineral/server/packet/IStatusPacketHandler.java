package gg.mineral.server.packet;

import dev.zerite.craftlib.protocol.Packet;
import dev.zerite.craftlib.protocol.connection.NettyConnection;
import dev.zerite.craftlib.protocol.packet.status.client.ClientStatusPingPacket;
import dev.zerite.craftlib.protocol.packet.status.client.ClientStatusRequestPacket;

public interface IStatusPacketHandler {

    public void handle(NettyConnection connection, Packet packet);

    public void handle(NettyConnection connection, ClientStatusRequestPacket packet);

    public void handle(NettyConnection connection, ClientStatusPingPacket packet);
}
