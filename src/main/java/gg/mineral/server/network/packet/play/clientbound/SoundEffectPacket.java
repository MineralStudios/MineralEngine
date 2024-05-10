package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public class SoundEffectPacket implements Packet.OUTGOING {
    String soundName;
    int x, y, z;
    float volume;
    short pitch;

    public SoundEffectPacket(String soundName, int x, int y, int z, float volume, short pitch) {
        this.soundName = soundName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public void serialize(ByteBuf os) {
        ByteBufUtil.writeString(os, soundName);
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
