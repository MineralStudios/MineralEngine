package gg.mineral.server.network.packet.play.bidirectional;

import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public class HeldItemChangePacket implements Packet.INCOMING, Packet.OUTGOING {

    short slot;

    public HeldItemChangePacket(short slot) {
        this.slot = slot;
    }

    @Override
    public void serialize(ByteBuf os) {
        os.writeByte(slot);
    }

    @Override
    public int getId() {
        return 0x09;
    }

    @Override
    public void received(Connection connection) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deserialize(ByteBuf is) {
        slot = is.readShort();
    }

}
