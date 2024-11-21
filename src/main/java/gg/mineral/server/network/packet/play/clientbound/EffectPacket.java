package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public record EffectPacket(int effectId, int x, short y, int z, int data, boolean disableRelativeVolume)
        implements Packet.OUTGOING {
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
    public byte getId() {
        return 0x28;
    }
}
