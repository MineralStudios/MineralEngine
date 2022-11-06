package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public class DisplayScoreboardPacket implements Packet.OUTGOING {

    @Override
    public void serialize(ByteBuf os) {
        // TODO continue with packet code
    }

    @Override
    public int getId() {
        return 0x3D;
    }

}
