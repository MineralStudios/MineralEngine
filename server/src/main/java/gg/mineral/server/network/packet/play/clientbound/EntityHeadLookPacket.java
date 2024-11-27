package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.api.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public final record EntityHeadLookPacket(int entityId, byte headYaw) implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(entityId);
        os.writeByte(headYaw);
    }

    @Override
    public byte getId() {
        return 0x19;
    }
}
