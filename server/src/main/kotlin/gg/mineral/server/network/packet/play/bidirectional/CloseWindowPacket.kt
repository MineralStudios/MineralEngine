package gg.mineral.server.network.packet.play.bidirectional

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

class CloseWindowPacket(var windowId: UByte = 0.toUByte()) : Packet.Incoming, Packet.Outgoing {

    override fun serialize(os: ByteBuf) {
        os.writeByte(windowId.toInt())
    }

    override fun deserialize(`is`: ByteBuf) {
        windowId = `is`.readByte().toUByte()
    }

    override val id: Byte
        get() = 0x2E
}
