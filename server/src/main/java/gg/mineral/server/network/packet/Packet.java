package gg.mineral.server.network.packet;

import gg.mineral.server.network.connection.Connection;
import io.netty.buffer.ByteBuf;

public interface Packet {

    public static interface OUTGOING extends Packet {
        public void serialize(ByteBuf os);

        public byte getId();
    }

    public static interface INCOMING extends Packet {
        public void received(Connection connection);

        public void deserialize(ByteBuf is);
    }
}
