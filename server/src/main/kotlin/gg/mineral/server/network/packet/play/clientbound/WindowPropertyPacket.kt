package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class WindowPropertyPacket(val windowId: Short, val property: Short, val value: Short) : Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        os.writeByte(windowId.toInt())
        os.writeShort(property, value)
    }

    override val id: Byte
        get() = 0x31
}
