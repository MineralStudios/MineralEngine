package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public class TimeUpdatePacket implements Packet.OUTGOING {
    long ageOfWorld, timeOfDay;

    public TimeUpdatePacket(long ageOfWorld, long timeOfDay) {
        this.ageOfWorld = ageOfWorld;
        this.timeOfDay = timeOfDay;
    }

    @Override
    public void serialize(ByteBuf os) {
        os.writeLong(ageOfWorld);
        os.writeLong(timeOfDay);
    }

    @Override
    public int getId() {
        return 0x03;
    }

}
