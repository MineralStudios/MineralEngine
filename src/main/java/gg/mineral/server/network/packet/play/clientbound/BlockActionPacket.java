package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public class BlockActionPacket implements Packet.OUTGOING {

    int x, z, blockType;
    short y;
    short byte1, byte2;

    public BlockActionPacket(int x, short y, int z, int blockType, short byte1, short byte2) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.blockType = blockType;
        this.byte1 = byte1;
        this.byte2 = byte2;
    }

    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(x);
        os.writeShort(y);
        os.writeInt(z);
        os.writeByte(byte1);
        os.writeByte(byte2);
        ByteBufUtil.writeVarInt(os, blockType);
    }

    @Override
    public int getId() {
        return 0x24;
    }

}
