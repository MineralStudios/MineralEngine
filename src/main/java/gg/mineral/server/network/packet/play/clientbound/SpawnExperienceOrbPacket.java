package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public class SpawnExperienceOrbPacket implements Packet.OUTGOING {
    int entityId, x, y, z;
    short count;

    public SpawnExperienceOrbPacket(int entityId, int x, int y, int z, short count) {
        this.entityId = entityId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.count = count;
    }

    @Override
    public void serialize(ByteBuf os) {
        ByteBufUtil.writeVarInt(os, entityId);
        os.writeInt(x);
        os.writeInt(y);
        os.writeInt(z);
        os.writeShort(count);
    }

    @Override
    public byte getId() {
        return 0x11;
    }

}
