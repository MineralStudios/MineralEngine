package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public record BlockActionPacket(int x, short y, int z, int blockType, short byte1, short byte2)
        implements Packet.OUTGOING {
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
    public byte getId() {
        return 0x24;
    }
}
