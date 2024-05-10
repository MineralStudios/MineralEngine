package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public class SetExperiencePacket implements Packet.OUTGOING {
    float experienceBar;
    short level, totalExperience;

    public SetExperiencePacket(float experienceBar, short level, short totalExperience) {
        this.experienceBar = experienceBar;
        this.level = level;
        this.totalExperience = totalExperience;
    }

    @Override
    public void serialize(ByteBuf os) {
        os.writeFloat(experienceBar);
        os.writeShort(level);
        os.writeShort(totalExperience);
    }

    @Override
    public byte getId() {
        return 0x1F;
    }

}
