package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class WindowPropertyPacket(val windowId: Short, val property: Short, val value: Short) : Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        os.writeByte(windowId.toInt())
        os.writeShort(property.toInt())
        os.writeShort(value.toInt())
    }

    override fun getId(): Byte {
        return 0x31
    }
}
