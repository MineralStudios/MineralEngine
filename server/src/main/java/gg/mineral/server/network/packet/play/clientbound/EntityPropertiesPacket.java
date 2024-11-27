package gg.mineral.server.network.packet.play.clientbound;

import java.util.Map;

import gg.mineral.api.entity.attribute.Attribute;
import gg.mineral.api.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public final record EntityPropertiesPacket(int entityId, Map<String, Attribute.Property> properties)
        implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(entityId);
        os.writeInt(properties.size());
        writeProperties(os, properties);
    }

    @Override
    public byte getId() {
        return 0x20;
    }

}
