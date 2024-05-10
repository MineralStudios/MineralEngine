package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public class EntityEffectPacket implements Packet.OUTGOING {
    int entityId;
    byte effectId, amplifier;
    short duration;

    public EntityEffectPacket(int entityId, byte effectId, byte amplifier, short duration) {
        this.entityId = entityId;
        this.effectId = effectId;
        this.amplifier = amplifier;
        this.duration = duration;
    }

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
