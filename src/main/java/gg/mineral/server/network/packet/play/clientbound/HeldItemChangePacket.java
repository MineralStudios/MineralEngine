package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public class HeldItemChangePacket implements Packet.OUTGOING {

    short slot;

    public HeldItemChangePacket(short slot) {
        this.slot = slot;
    }

    @Override
    public void serialize(ByteBuf os) {
        os.writeShort(slot);
    }

    @Override
    public int getId() {
        return 0x09;
    }

}
