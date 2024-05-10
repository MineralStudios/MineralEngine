package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public class RemoveEntityEffectPacket implements Packet.OUTGOING {
    int entityId;
    byte effectId;

    public RemoveEntityEffectPacket(int entityId, byte effectId) {
        this.entityId = entityId;
        this.effectId = effectId;
    }

    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(effectId);
        os.writeByte(effectId);
    }

    @Override
    public byte getId() {
        return 0x1E;
    }

}
