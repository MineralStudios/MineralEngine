package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public class PlayerAbilitiesPacket implements Packet.OUTGOING {
    byte flags;
    float flyingSpeed, walkingSpeed;

    public PlayerAbilitiesPacket(byte flags, float flyingSpeed, float walkingSpeed) {
        this.flags = flags;
        this.flyingSpeed = flyingSpeed;
        this.walkingSpeed = walkingSpeed;
    }

    @Override
    public void serialize(ByteBuf os) {
        os.writeByte(flags);
        os.writeFloat(flyingSpeed);
        os.writeFloat(walkingSpeed);
    }

    @Override
    public int getId() {
        return 0x39;
    }

}
