package gg.mineral.server.network.packet;

import gg.mineral.server.network.connection.Connection;
import io.netty.buffer.ByteBuf;

public class Packet {

    public static interface OUTGOING {
        public void serialize(ByteBuf os);

        public byte getId();
    }

    public static interface INCOMING {
        public void received(Connection connection);

        public void deserialize(ByteBuf is);
    }
}
