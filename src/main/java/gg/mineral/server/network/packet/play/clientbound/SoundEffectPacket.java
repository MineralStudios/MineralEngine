package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public class SoundEffectPacket implements Packet.OUTGOING {
    String soundName;
    int x, y, z;
    float volume;
    short pitch;

    public SoundEffectPacket(String soundName, double x, double y, double z, float volume, float pitch) {
        this.soundName = soundName;
        this.x = (int) (x * 8);
        this.y = (int) (y * 8);
        this.z = (int) (z * 8);
        this.volume = volume;
        this.pitch = (short) (pitch * 63);
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
