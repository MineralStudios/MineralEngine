package gg.mineral.server.network.packet.play.serverbound;

import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public class UseEntityPacket implements Packet.INCOMING {

    int target;
    byte mouse;

    @Override
    public void received(Connection connection) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deserialize(ByteBuf is) {
        target = is.readInt();
        mouse = is.readByte();
    }

    @Override
    public int getId() {
        return 0x02;
    }

}
