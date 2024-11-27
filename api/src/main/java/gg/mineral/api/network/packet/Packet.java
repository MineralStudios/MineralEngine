package gg.mineral.api.network.packet;

import gg.mineral.api.network.connection.Connection;
import gg.mineral.api.network.packet.rw.ByteReader;
import gg.mineral.api.network.packet.rw.ByteWriter;
import io.netty.buffer.ByteBuf;

public interface Packet {

    public static interface OUTGOING extends Packet, ByteWriter {
        /**
         * Serialize the packet to the output stream.
         * 
         * @param os
         */
        void serialize(ByteBuf os);

        /**
         * Get the packet ID.
         * 
         * @return The packet ID.
         */
        byte getId();
    }

    public static interface INCOMING extends Packet, ByteReader {
        /**
         * Handle the packet when it is received.
         * 
         * @param connection
         */
        void received(Connection connection);

        /**
         * Deserialize the packet from the input stream.
         * 
         * @param is
         */
        void deserialize(ByteBuf is);
    }
}
