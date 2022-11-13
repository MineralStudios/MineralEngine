package gg.mineral.server.network.packet.play.clientbound;

import java.util.List;

import gg.mineral.server.entity.EntityMetadata;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public class EntityMetadataPacket implements Packet.OUTGOING {

    int entityId;
    List<EntityMetadata.Entry> entries;

    public EntityMetadataPacket(int entityId, List<EntityMetadata.Entry> entries) {
        this.entityId = entityId;
        this.entries = entries;
    }

    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(entityId);
        ByteBufUtil.writeMetadata(os, entries);
    }

    @Override
    public int getId() {
        return 0x1C;
    }

}
