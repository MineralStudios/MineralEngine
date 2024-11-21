package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public record WindowPropertyPacket(short windowId, short property, short value) implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        os.writeByte(windowId);
        os.writeShort(property);
        os.writeShort(value);
    }

    @Override
    public byte getId() {
        return 0x31;
    }
}
