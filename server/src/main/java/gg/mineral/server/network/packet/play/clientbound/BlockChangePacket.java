package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.api.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public final record BlockChangePacket(int x, short y, int z, int blockId, short blockMetadata)
        implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(x);
        os.writeByte(y);
        os.writeInt(z);
        writeVarInt(os, blockId);
        os.writeByte(blockMetadata);
    }

    @Override
    public byte getId() {
        return 0x23;
    }
}
