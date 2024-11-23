package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public record EntityTeleportPacket(int entityId, int x, int y, int z, byte yaw, byte pitch) implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(entityId);
        os.writeInt(x);
        os.writeInt(y);
        os.writeInt(z);
        os.writeByte(yaw);
        os.writeByte(pitch);
    }

    @Override
    public byte getId() {
        return 0x18;
    }
}
