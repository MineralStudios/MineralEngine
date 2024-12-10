package gg.mineral.server.network.packet.play.bidirectional

import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

class KeepAlivePacket(var keepAliveId: Int = 0) : Packet.ASYNC_INCOMING, Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        os.writeInt(keepAliveId)
    }

    override fun received(connection: Connection) {
    }

    override fun deserialize(`is`: ByteBuf) {
        keepAliveId = `is`.readInt()
    }

    override val id: Byte
        get() = 0x00

    override fun receivedAsync(connection: Connection) {
    }
}
