package gg.mineral.server.network.packet.play.clientbound;

import java.util.List;

import gg.mineral.server.entity.metadata.EntityMetadata;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.math.MathUtil;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public class SpawnMobPacket implements Packet.OUTGOING {
    int entityId, x, y, z;
    short type, velocityX, velocityY, velocityZ;
    byte yaw, pitch, headPitch;
    List<EntityMetadata.Entry> entries;

    public SpawnMobPacket(int entityId, double x, double y, double z, byte yaw, byte pitch, byte headPitch,
            List<EntityMetadata.Entry> entries) {
        this.entityId = entityId;
        this.x = MathUtil.toFixedPointInt(x);
        this.y = MathUtil.toFixedPointInt(y);
        this.z = MathUtil.toFixedPointInt(z);
        this.yaw = yaw;
        this.pitch = pitch;
        this.headPitch = headPitch;
        this.entries = entries;
    }

    @Override
    public void serialize(ByteBuf os) {
        ByteBufUtil.writeVarInt(os, entityId);
        os.writeByte(type);
        os.writeInt(x);
        os.writeInt(y);
        os.writeInt(z);
        os.writeByte(yaw);
        os.writeByte(pitch);
        os.writeByte(headPitch);
        os.writeShort(velocityX);
        os.writeShort(velocityY);
        os.writeShort(velocityZ);
        ByteBufUtil.writeMetadata(os, entries);
    }

    @Override
    public int getId() {
        return 0x0F;
    }

}
