package gg.mineral.server.network.packet.play.bidirectional

import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

class CloseWindowPacket(var windowId: UByte = 0.toUByte()) : Packet.INCOMING, Packet.OUTGOING {

    override fun serialize(os: ByteBuf) {
        os.writeByte(windowId.toInt())
    }

    override fun received(connection: Connection) {
        // TODO Auto-generated method stub
    }

    override fun deserialize(`is`: ByteBuf) {
        windowId = `is`.readByte().toUByte()
    }

    override val id: Byte
        get() = 0x2E
}
