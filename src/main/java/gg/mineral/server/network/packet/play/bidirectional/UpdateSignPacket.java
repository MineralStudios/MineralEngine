package gg.mineral.server.network.packet.play.bidirectional;

import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UpdateSignPacket implements Packet.INCOMING, Packet.OUTGOING {
    int x, z;
    short y;
    String line1, line2, line3, line4;

    public UpdateSignPacket(int x, short y, int z, String line1, String line2, String line3, String line4) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.line1 = line1;
        this.line2 = line2;
        this.line3 = line3;
        this.line4 = line4;
    }

    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(x);
        os.writeShort(y);
        os.writeInt(z);
        ByteBufUtil.writeString(os, line1);
        ByteBufUtil.writeString(os, line2);
        ByteBufUtil.writeString(os, line3);
        ByteBufUtil.writeString(os, line4);
    }

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
    public byte getId() {
        return 0x33;
    }

}
