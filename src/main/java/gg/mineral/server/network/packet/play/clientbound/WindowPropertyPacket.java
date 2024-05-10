package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public class WindowPropertyPacket implements Packet.OUTGOING {
    short windowId, property, value;

    public WindowPropertyPacket(short windowId, short property, short value) {
        this.windowId = windowId;
        this.property = property;
        this.value = value;
    }

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
