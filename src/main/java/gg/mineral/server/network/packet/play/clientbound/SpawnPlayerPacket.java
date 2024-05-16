package gg.mineral.server.network.packet.play.clientbound;

import java.util.List;

import gg.mineral.server.entity.living.human.property.PlayerProperty;
import gg.mineral.server.entity.metadata.EntityMetadata;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.math.MathUtil;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public class SpawnPlayerPacket implements Packet.OUTGOING {
    int entityId, x, y, z;
    String playerUUID, playerName;
    List<PlayerProperty> playerProperties;
    byte yaw, pitch;
    short currentItem;
    List<EntityMetadata.Entry> entries;

    public SpawnPlayerPacket(int entityId, int x, int y, int z, float yaw, float pitch, String playerUUID,
            String playerName,
            List<PlayerProperty> playerProperties, short currentItem,
            List<EntityMetadata.Entry> entries) {
        this.entityId = entityId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.playerProperties = playerProperties;
        this.yaw = MathUtil.angleToByte(yaw);
        this.pitch = MathUtil.angleToByte(pitch);
        this.currentItem = currentItem;
        this.entries = entries;
    }

    @Override
    public void serialize(ByteBuf os) {
        ByteBufUtil.writeVarInt(os, entityId);
        ByteBufUtil.writeString(os, playerUUID);
        ByteBufUtil.writeString(os, playerName);
        ByteBufUtil.writeVarInt(os, playerProperties.size());

        for (PlayerProperty playerProperty : playerProperties) {
            ByteBufUtil.writeString(os, playerProperty.getName());
            ByteBufUtil.writeString(os, playerProperty.getValue());
            ByteBufUtil.writeString(os, playerProperty.getSignature());
        }

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
