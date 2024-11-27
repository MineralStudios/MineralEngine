package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.api.network.packet.Packet;

import io.netty.buffer.ByteBuf;

public final record SoundEffectPacket(String soundName, int x, int y, int z, float volume, short pitch)
        implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        writeString(os, soundName);
        os.writeInt(x);
        os.writeInt(y);
        os.writeInt(z);
        os.writeFloat(volume);
        os.writeByte(pitch);
    }

    @Override
    public byte getId() {
        return 0x29;
    }
}
