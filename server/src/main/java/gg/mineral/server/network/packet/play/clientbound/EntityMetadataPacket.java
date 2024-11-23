package gg.mineral.server.network.packet.play.clientbound;

import java.util.List;

import gg.mineral.server.entity.metadata.EntityMetadata;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public record EntityMetadataPacket(int entityId, List<EntityMetadata.Entry> entries) implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(entityId);
        ByteBufUtil.writeMetadata(os, entries);
    }

    @Override
    public byte getId() {
        return 0x1C;
    }
}
