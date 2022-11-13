package gg.mineral.server.network.packet.play.serverbound;

import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public class UpdateSignPacket implements Packet.INCOMING {
    int x, z;
    short y;
    String line1, line2, line3, line4;

    @Override
    public void received(Connection connection) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deserialize(ByteBuf is) {
        x = is.readInt();
        y = is.readShort();
        z = is.readInt();
        line1 = ByteBufUtil.readString(is);
        line2 = ByteBufUtil.readString(is);
        line3 = ByteBufUtil.readString(is);
        line4 = ByteBufUtil.readString(is);
    }

    @Override
    public int getId() {
        return 0x12;
    }

}
