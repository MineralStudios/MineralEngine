package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class ChangeGameStatePacket(val reason: Short, val value: Float) : Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        os.writeByte(reason.toInt())
        os.writeFloat(value)
    }

    override val id: Byte
        get() = 0x2B
}
