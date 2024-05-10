package gg.mineral.server.network.packet.play.clientbound;

import java.util.List;

import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public class MultiBlockChangePacket implements Packet.OUTGOING {
    int chunkX, chunkZ;
    List<BlockChangePacket> records;

    public MultiBlockChangePacket(int chunkX, int chunkZ, List<BlockChangePacket> records) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.records = records;
    }

    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(chunkX);
        os.writeInt(chunkZ);
        os.writeShort(records.size());
        os.writeInt(records.size() * 4);

        for (BlockChangePacket record : records) {
            // XZYYTTTM
            int value = (record.getMetadata() & 0xF) |
                    ((record.getType() & 0xFFF) << 4) |
                    ((record.getY() & 0xFF) << 16) |
                    ((record.getZ() & 0xF) << 24) |
                    ((record.getX() & 0xF) << 28);
            os.writeInt(value);
        }
    }

    @Override
    public byte getId() {
        return 0x22;
    }

}
