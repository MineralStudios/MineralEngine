package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public record SpawnObjectPacket(int entityId, byte type, int x, int y, int z, byte pitch, byte yaw, int data)
        implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        ByteBufUtil.writeVarInt(os, entityId);
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
