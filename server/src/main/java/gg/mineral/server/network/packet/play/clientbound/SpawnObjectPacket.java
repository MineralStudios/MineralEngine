package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.api.network.packet.Packet;

import io.netty.buffer.ByteBuf;

public final record SpawnObjectPacket(int entityId, byte type, int x, int y, int z, byte pitch, byte yaw, int data)
        implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        writeVarInt(os, entityId);
        os.writeByte(type);
        os.writeInt(x);
        os.writeInt(y);
        os.writeInt(z);
        os.writeByte(pitch);
        os.writeByte(yaw);
        os.writeInt(data);
    }

    @Override
    public byte getId() {
        return 0x0E;
    }
}
