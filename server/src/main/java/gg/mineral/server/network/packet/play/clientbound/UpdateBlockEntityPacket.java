package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public record UpdateBlockEntityPacket(int x, short y, int z, short action, byte[] nbtData) implements Packet.OUTGOING {

    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(x);
        os.writeShort(y);
        os.writeInt(z);
        os.writeByte(action);
        os.writeShort(nbtData.length);
        os.writeBytes(nbtData);
    }

    @Override
    public byte getId() {
        return 0x35;
    }
}
