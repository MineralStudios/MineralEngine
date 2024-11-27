package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.api.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public final record UpdateHealthPacket(float health, short hunger, float saturation) implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        os.writeFloat(health);
        os.writeShort(hunger);
        os.writeFloat(saturation);
    }

    @Override
    public byte getId() {
        return 0x06;
    }
}
