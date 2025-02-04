package gg.mineral.server.network.packet.play.bidirectional

import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.Packet
import gg.mineral.server.network.connection.ConnectionImpl
import io.netty.buffer.ByteBuf

class KeepAlivePacket(var keepAliveId: Int = 0) : Packet.Incoming, Packet.AsyncHandler, Packet.Outgoing {
    override fun serialize(os: ByteBuf) {
        os.writeInt(keepAliveId)
    }

    override fun deserialize(`is`: ByteBuf) {
        keepAliveId = `is`.readInt()
    }

    override val id: Byte
        get() = 0x00

    override suspend fun receivedAsync(connection: Connection) {
        if (connection is ConnectionImpl) {
            val time = connection.serverSnapshot.millis - connection.lastKeepAlive
            connection.ping = ((connection.ping * 3 + time) / 4).toInt();
        }
    }
}
