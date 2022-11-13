package gg.mineral.server.network.packet.play.serverbound;

import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public class PlayerDiggingPacket implements Packet.INCOMING {
    byte status, face;
    int x, z;
    short y;

    @Override
    public void received(Connection connection) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deserialize(ByteBuf is) {
        status = is.readByte();
        x = is.readInt();
        y = is.readUnsignedByte();
        z = is.readInt();
        face = is.readByte();
    }

}
