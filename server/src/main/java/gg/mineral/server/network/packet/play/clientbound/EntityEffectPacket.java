package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.api.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public final record EntityEffectPacket(int entityId, byte effectId, byte amplifier, short duration)
        implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(entityId);
        os.writeByte(effectId);
        os.writeByte(amplifier);
        os.writeShort(duration);
    }

    @Override
    public byte getId() {
        return 0x1D;
    }
}
