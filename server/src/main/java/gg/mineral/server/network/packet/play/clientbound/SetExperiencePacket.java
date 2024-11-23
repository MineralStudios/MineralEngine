package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public record SetExperiencePacket(float experienceBar, short level, short totalExperience) implements Packet.OUTGOING {
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
