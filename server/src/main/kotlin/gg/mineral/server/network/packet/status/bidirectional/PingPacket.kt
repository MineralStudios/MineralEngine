package gg.mineral.server.network.packet.status.bidirectional

import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

class PingPacket(var time: Long = 0) : Packet.Incoming, Packet.Outgoing, Packet.EventLoopHandler {
    override val id: Byte
        get() = 0x01

    override suspend fun receivedEventLoop(connection: Connection) =
        connection.queuePacket(this)

    override fun deserialize(`is`: ByteBuf) {
        time = `is`.readLong()
    }

    override fun serialize(os: ByteBuf) {
        os.writeLong(time)
    }
}
