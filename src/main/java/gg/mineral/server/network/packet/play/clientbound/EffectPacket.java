package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public class EffectPacket implements Packet.OUTGOING {
    int effectId, x, z, data;
    byte y;
    boolean disableRelativeVolume;

    public EffectPacket(int effectId, int x, byte y, int z, int data, boolean disableRelativeVolume) {
        this.effectId = effectId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.data = data;
        this.disableRelativeVolume = disableRelativeVolume;
    }

    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(effectId);
        os.writeInt(x);
        os.writeByte(y);
        os.writeInt(z);
        os.writeInt(data);
        os.writeBoolean(disableRelativeVolume);
    }

    @Override
    public int getId() {
        return 0x28;
    }

}
