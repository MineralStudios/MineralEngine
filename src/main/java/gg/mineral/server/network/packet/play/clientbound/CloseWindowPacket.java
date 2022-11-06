package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public class CloseWindowPacket implements Packet.OUTGOING {
    short windowId;

    public CloseWindowPacket(short windowId) {
        this.windowId = windowId;
    }

    @Override
    public void serialize(ByteBuf os) {
        os.writeByte(windowId);
    }

    @Override
    public int getId() {
        return 0x2E;
    }

}
