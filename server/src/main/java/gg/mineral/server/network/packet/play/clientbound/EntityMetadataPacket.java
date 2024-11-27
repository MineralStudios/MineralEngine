package gg.mineral.server.network.packet.play.clientbound;

import java.util.List;

import gg.mineral.api.network.packet.Packet;
import gg.mineral.server.entity.meta.EntityMetadataImpl;
import io.netty.buffer.ByteBuf;

public final record EntityMetadataPacket(int entityId, List<EntityMetadataImpl.Entry> entries)
        implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(entityId);
        writeMetadata(os, entries);
    }

    @Override
    public byte getId() {
        return 0x1C;
    }
}
