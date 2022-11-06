package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public class SetSlotPacket implements Packet.OUTGOING {

    @Override
    public void serialize(ByteBuf os) {

    }

    @Override
    public int getId() {
        return 0x2F;
    }

}
