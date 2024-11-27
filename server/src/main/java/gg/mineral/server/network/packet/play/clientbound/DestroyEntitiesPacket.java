package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.api.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public final record DestroyEntitiesPacket(int[] entityIds) implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        os.writeByte(entityIds.length);
        writeIntArray(os, entityIds);
    }

    @Override
    public byte getId() {
        return 0x13;
    }
}
