package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.api.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public final record EntityVelocityPacket(int entityId, short x, short y, short z) implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(entityId);
        os.writeShort(x);
        os.writeShort(y);
        os.writeShort(z);
    }

    @Override
    public byte getId() {
        return 0x12;
    }

}
