package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.api.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public final record BlockBreakAnimationPacket(int entityId, int x, int y, int z, byte destroyStage)
        implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        writeVarInt(os, entityId);
        os.writeInt(x);
        os.writeInt(y);
        os.writeInt(z);
        os.writeByte(destroyStage);
    }

    @Override
    public byte getId() {
        return 0x25;
    }
}
