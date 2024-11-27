package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.api.network.packet.Packet;

import io.netty.buffer.ByteBuf;

public final record SpawnPaintingPacket(int entityId, String title, int x, int y, int z, int direction)
        implements Packet.OUTGOING {

    @Override
    public void serialize(ByteBuf os) {
        writeVarInt(os, entityId);
        writeString(os, title);
        os.writeInt(x);
        os.writeInt(y);
        os.writeInt(z);
        os.writeInt(direction);
    }

    @Override
    public byte getId() {
        return 0x10;
    }
}
