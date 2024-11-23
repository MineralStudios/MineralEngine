package gg.mineral.server.network.packet.play.clientbound;

import java.util.List;

import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.val;

public record MultiBlockChangePacket(int chunkX, int chunkZ, List<BlockChangePacket> records)
        implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(chunkX);
        os.writeInt(chunkZ);
        os.writeShort(records.size());
        os.writeInt(records.size() * 4);

        for (val record : records) {
            // XZYYTTTM
            int value = (record.blockMetadata() & 0xF) |
                    ((record.blockId() & 0xFFF) << 4) |
                    ((record.y() & 0xFF) << 16) |
                    ((record.z() & 0xF) << 24) |
                    ((record.x() & 0xF) << 28);
            os.writeInt(value);
        }
    }

    @Override
    public byte getId() {
        return 0x22;
    }
}
