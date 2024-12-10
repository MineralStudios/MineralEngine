package gg.mineral.server.network.packet.play.bidirectional

import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

class HeldItemChangePacket(var slot: Short = 0) : Packet.INCOMING, Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        os.writeByte(slot.toInt())
    }

    override val id: Byte
        get() = 0x09

    override fun received(connection: Connection) {
        // TODO Auto-generated method stub
    }

    override fun deserialize(`is`: ByteBuf) {
        slot = `is`.readShort()
    }
}
