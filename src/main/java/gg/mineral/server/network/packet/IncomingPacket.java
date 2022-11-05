package gg.mineral.server.network.packet;

import gg.mineral.server.network.connection.Connection;
import io.netty.buffer.ByteBuf;

public abstract class IncomingPacket extends Packet {
    public abstract void received(Connection connection);

    public abstract void deserialize(ByteBuf is);
}
