package gg.mineral.api.network.packet

import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.rw.ByteReader
import gg.mineral.api.network.packet.rw.ByteWriter
import io.netty.buffer.ByteBuf

interface Packet {
    interface OUTGOING : Packet, ByteWriter {
        /**
         * Serialize the packet to the output stream.
         *
         * @param os
         */
        fun serialize(os: ByteBuf?)

        /**
         * Get the packet ID.
         *
         * @return The packet ID.
         */
        val id: Byte
    }

    interface INCOMING : Packet, ByteReader {
        /**
         * Handle the packet when it is received.
         *
         * @param connection
         */
        fun received(connection: Connection?)

        /**
         * Deserialize the packet from the input stream.
         *
         * @param is
         */
        fun deserialize(`is`: ByteBuf?)
    }

    interface ASYNC_INCOMING : INCOMING {
        /**
         * Handle the packet when it is received asynchronously.
         *
         * @param connection
         */
        fun receivedAsync(connection: Connection?)
    }
}
