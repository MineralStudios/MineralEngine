package gg.mineral.server.network.packet.play.bidirectional

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

class HeldItemChangePacket(var slot: Short = 0) : Packet.Incoming, Packet.Outgoing {
    override fun serialize(os: ByteBuf) {
        os.writeByte(slot.toInt())
    }

    override val id: Byte
        get() = 0x09

    override fun deserialize(`is`: ByteBuf) {
        slot = `is`.readShort()
    }
}
