package gg.mineral.server.network.packet.status.clientbound;

import gg.mineral.api.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public final record ResponsePacket(String jsonResponse) implements Packet.OUTGOING {

    @Override
    public void serialize(ByteBuf os) {
        writeString(os, jsonResponse);
    }

    @Override
    public byte getId() {
        return 0x00;
    }
}
