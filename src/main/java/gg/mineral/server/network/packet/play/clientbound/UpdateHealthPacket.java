package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public class UpdateHealthPacket implements Packet.OUTGOING {
    float health, saturation;
    short hunger;

    public UpdateHealthPacket(float health, short hunger, float saturation) {
        this.health = health;
        this.hunger = hunger;
        this.saturation = saturation;
    }

    @Override
    public void serialize(ByteBuf os) {
        os.writeFloat(health);
        os.writeShort(hunger);
        os.writeFloat(saturation);
    }

    @Override
    public int getId() {
        return 0x06;
    }

}
