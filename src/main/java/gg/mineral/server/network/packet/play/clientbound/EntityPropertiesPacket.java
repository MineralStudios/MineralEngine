package gg.mineral.server.network.packet.play.clientbound;

import java.util.Map;

import gg.mineral.server.entity.attribute.Property;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public class EntityPropertiesPacket implements Packet.OUTGOING {

    int entityId;
    Map<String, Property> properties;

    public EntityPropertiesPacket(int entityId, Map<String, Property> properties) {
        this.entityId = entityId;
        this.properties = properties;
    }

    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(entityId);
        os.writeInt(properties.size());
        ByteBufUtil.writeProperties(os, properties);
    }

    @Override
    public int getId() {
        return 0x20;
    }

}
