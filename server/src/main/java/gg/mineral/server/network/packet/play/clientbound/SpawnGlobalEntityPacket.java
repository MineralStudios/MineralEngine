package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.api.network.packet.Packet;

import io.netty.buffer.ByteBuf;

public final record SpawnGlobalEntityPacket(int entityId, byte type, int x, int y, int z) implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        writeVarInt(os, entityId);
        os.writeByte(type);
        os.writeInt(x);
        os.writeInt(y);
        os.writeInt(z);
    }

    @Override
    public byte getId() {
        return 0x2C;
    }
}
