package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.api.network.packet.Packet;

import io.netty.buffer.ByteBuf;

public final record SpawnExperienceOrbPacket(int entityId, int x, int y, int z, short count)
        implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        writeVarInt(os, entityId);
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
