package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public record TimeUpdatePacket(long ageOfWorld, long timeOfDay) implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        os.writeLong(ageOfWorld);
        os.writeLong(timeOfDay);
    }

    @Override
    public byte getId() {
        return 0x03;
    }
}
