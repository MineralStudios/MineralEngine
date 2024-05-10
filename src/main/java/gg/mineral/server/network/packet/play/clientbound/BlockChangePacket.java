package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public class BlockChangePacket implements Packet.OUTGOING {
    int x, z, blockId;
    short y, blockMetadata;

    public BlockChangePacket(int x, short y, int z, int blockId, short blockMetadata) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.blockId = blockId;
        this.blockMetadata = blockMetadata;
    }

    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(x);
        os.writeByte(y);
        os.writeInt(z);
        ByteBufUtil.writeVarInt(os, blockId);
        os.writeByte(blockMetadata);
    }

    @Override
    public byte getId() {
        return 0x23;
    }

    public short getMetadata() {
        return blockMetadata;
    }

    public int getType() {
        return blockId;
    }

    public short getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getX() {
        return x;
    }

}
