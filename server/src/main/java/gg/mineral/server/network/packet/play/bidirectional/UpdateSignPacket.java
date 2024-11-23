package gg.mineral.server.network.packet.play.bidirectional;

import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(fluent = true)
public class UpdateSignPacket implements Packet.INCOMING, Packet.OUTGOING {
    private int x;
    private short y;
    private int z;
    private String line1, line2, line3, line4;

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
