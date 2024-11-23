package gg.mineral.server.network.packet.play.clientbound;

import java.util.List;

import gg.mineral.server.entity.metadata.EntityMetadata;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public record SpawnMobPacket(
        int entityId,
        short type,
        int x,
        int y,
        int z,
        byte yaw,
        byte pitch,
        byte headPitch,
        short velocityX,
        short velocityY,
        short velocityZ,
        List<EntityMetadata.Entry> entries) implements Packet.OUTGOING {
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
    public byte getId() {
        return 0x0F;
    }
}
