package gg.mineral.server.network.packet.play.clientbound;

import java.util.List;

import gg.mineral.server.entity.living.human.property.PlayerProperty;
import gg.mineral.server.entity.metadata.EntityMetadata;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;
import lombok.val;

public record SpawnPlayerPacket(
        int entityId,
        int x,
        int y,
        int z,
        byte yaw,
        byte pitch, String playerUUID,
        String playerName,
        List<PlayerProperty> playerProperties,
        short currentItem,
        List<EntityMetadata.Entry> entries) implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        ByteBufUtil.writeVarInt(os, entityId);
        ByteBufUtil.writeString(os, playerUUID);
        ByteBufUtil.writeString(os, playerName);
        ByteBufUtil.writeVarInt(os, playerProperties.size());

        // TODO: Make it more concise like below
        for (val playerProperty : playerProperties)
            ByteBufUtil.writeString(os, playerProperty.name(), playerProperty.value(), playerProperty.signature());

        os.writeInt(x);
        os.writeInt(y);
        os.writeInt(z);
        os.writeByte(yaw);
        os.writeByte(pitch);
        os.writeShort(currentItem);
        ByteBufUtil.writeMetadata(os, entries);
    }

    @Override
    public byte getId() {
        return 0x0C;
    }

}
