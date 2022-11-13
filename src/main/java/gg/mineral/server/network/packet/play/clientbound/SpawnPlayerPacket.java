package gg.mineral.server.network.packet.play.clientbound;

import java.util.List;

import gg.mineral.server.entity.PlayerProperty;
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

    public SpawnPlayerPacket(int entityId, double x, double y, double z, String playerUUID, String playerName,
            List<PlayerProperty> playerProperties, byte yaw, byte pitch, short currentItem,
            List<EntityMetadata.Entry> entries) {
        this.entityId = entityId;
        this.x = MathUtil.toFixedPointInt(x);
        this.y = MathUtil.toFixedPointInt(y);
        this.z = MathUtil.toFixedPointInt(z);
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.playerProperties = playerProperties;
        this.yaw = yaw;
        this.pitch = pitch;
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
    public int getId() {
        return 0x0C;
    }

}
