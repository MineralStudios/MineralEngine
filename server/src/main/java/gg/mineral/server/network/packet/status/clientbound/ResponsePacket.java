package gg.mineral.server.network.packet.status.clientbound;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public record ResponsePacket(String jsonResponse) implements Packet.OUTGOING {

    @Override
    public void serialize(ByteBuf os) {
        ByteBufUtil.writeString(os, jsonResponse);
    }

    @Override
    public byte getId() {
        return 0x00;
    }
}
