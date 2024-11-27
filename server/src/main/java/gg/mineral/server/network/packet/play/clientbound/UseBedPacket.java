package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.api.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public final record UseBedPacket(int entityId, int x, short y, int z) implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(entityId);
        os.writeInt(x);
        os.writeByte(y);
        os.writeInt(z);
    }

    @Override
    public byte getId() {
        return 0x0A;
    }
}
