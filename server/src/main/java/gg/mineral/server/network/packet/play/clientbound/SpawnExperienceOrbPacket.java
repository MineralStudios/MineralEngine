package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public record SpawnExperienceOrbPacket(int entityId, int x, int y, int z, short count) implements Packet.OUTGOING {
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
