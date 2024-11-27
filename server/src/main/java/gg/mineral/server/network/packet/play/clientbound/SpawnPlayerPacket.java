package gg.mineral.server.network.packet.play.clientbound;

import java.util.List;

import gg.mineral.api.entity.living.human.property.PlayerProperty;
import gg.mineral.api.network.packet.Packet;
import gg.mineral.server.entity.meta.EntityMetadataImpl;
import io.netty.buffer.ByteBuf;
import lombok.val;

public final record SpawnPlayerPacket(
        int entityId,
        int x,
        int y,
        int z,
        byte yaw,
        byte pitch, String playerUUID,
        String playerName,
        List<PlayerProperty> playerProperties,
        short currentItem,
        List<EntityMetadataImpl.Entry> entries) implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        writeVarInt(os, entityId);
        writeString(os, playerUUID);
        writeString(os, playerName);
        writeVarInt(os, playerProperties.size());

        // TODO: Make it more concise like below
        for (val playerProperty : playerProperties)
            writeString(os, playerProperty.name(), playerProperty.value(), playerProperty.signature());

        os.writeInt(x);
        os.writeInt(y);
        os.writeInt(z);
        os.writeByte(yaw);
        os.writeByte(pitch);
        os.writeShort(currentItem);
        writeMetadata(os, entries);
    }

    @Override
    public byte getId() {
        return 0x0C;
    }

}
