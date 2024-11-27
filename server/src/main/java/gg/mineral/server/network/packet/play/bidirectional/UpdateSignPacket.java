package gg.mineral.server.network.packet.play.bidirectional;

import gg.mineral.api.network.connection.Connection;
import gg.mineral.api.network.packet.Packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(fluent = true)
public final class UpdateSignPacket implements Packet.INCOMING, Packet.OUTGOING {
    private int x;
    private short y;
    private int z;
    private String line1, line2, line3, line4;

    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(x);
        os.writeShort(y);
        os.writeInt(z);
        writeString(os, line1, line2, line3, line4);
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
        line1 = readString(is);
        line2 = readString(is);
        line3 = readString(is);
        line4 = readString(is);
    }

    @Override
    public byte getId() {
        return 0x33;
    }
}
