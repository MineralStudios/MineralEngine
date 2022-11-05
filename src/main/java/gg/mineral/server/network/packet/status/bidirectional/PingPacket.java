package gg.mineral.server.network.packet.status.bidirectional;

import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.BidirectionalPacket;
import io.netty.buffer.ByteBuf;

public class PingPacket extends BidirectionalPacket {

    long time;

    public PingPacket() {
    }

    public PingPacket(long time) {
        this.time = time;
    }

    @Override
    public int getId() {
        return 0x01;
    }

    @Override
    public void received(Connection connection) {
        connection.sendPacket(this);
    }

    @Override
    public void deserialize(ByteBuf is) {
        time = is.readLong();
    }

    @Override
    public void serialize(ByteBuf os) {
        os.writeLong(time);
    }

}
