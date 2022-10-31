package gg.mineral.server.packet;

import dev.zerite.craftlib.protocol.Packet;
import dev.zerite.craftlib.protocol.connection.NettyConnection;
import dev.zerite.craftlib.protocol.packet.login.client.ClientLoginEncryptionResponsePacket;
import dev.zerite.craftlib.protocol.packet.login.client.ClientLoginStartPacket;

public interface ILoginPacketHandler {
    public void handle(NettyConnection connection, Packet packet);

    public void handle(NettyConnection connection, ClientLoginStartPacket packet);

    public void handle(NettyConnection connection, ClientLoginEncryptionResponsePacket packet);
}
