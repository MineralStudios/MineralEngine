package gg.mineral.api.network.packet

import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.rw.ByteReader
import gg.mineral.api.network.packet.rw.ByteWriter
import io.netty.buffer.ByteBuf

interface Packet {
    interface Outgoing : Packet, ByteWriter {
        /**
         * Serialize the packet to the output stream.
         *
         * @param os
         */
        fun serialize(os: ByteBuf)

        /**
         * Get the packet ID.
         *
         * @return The packet ID.
         */
        val id: Byte
    }

    interface Incoming : Packet, ByteReader {
        /**
         * Deserialize the packet from the input stream.
         *
         * @param is
         */
        fun deserialize(`is`: ByteBuf)
    }

    interface SyncHandler {
        /**
         * Handle the packet when it is received.
         *
         * @param connection
         */
        suspend fun receivedSync(connection: Connection)
    }

    interface AsyncHandler {
        /**
         * Handle the packet when it is received asynchronously.
         *
         * @param connection
         */
        suspend fun receivedAsync(connection: Connection)
    }

    interface EventLoopHandler {
        /**
         * Handle the packet when it is received on the event loop.
         *
         * @param connection
         */
        suspend fun receivedEventLoop(connection: Connection)
    }
}
