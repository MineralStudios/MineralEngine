package gg.mineral.server.network.packet.play.bidirectional;

import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public class CloseWindowPacket implements Packet.INCOMING, Packet.OUTGOING {
    short windowId;

    public CloseWindowPacket(short windowId) {
        this.windowId = windowId;
    }

    public CloseWindowPacket() {
    }

    @Override
    public void serialize(ByteBuf os) {
        os.writeByte(windowId);
    }

    @Override
    public void received(Connection connection) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deserialize(ByteBuf is) {
        windowId = is.readByte();
    }

    @Override
    public int getId() {
        return 0x2E;
    }

}
