package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.api.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public final record RemoveEntityEffectPacket(int entityId, byte effectId) implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(entityId);
        os.writeByte(effectId);
    }

    @Override
    public byte getId() {
        return 0x1E;
    }
}
