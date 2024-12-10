package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class DisplayScoreboardPacket(val position: Byte, val scoreName: String) : Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        os.writeByte(position.toInt())
        writeString(os, scoreName)
    }

    override val id: Byte
        get() = 0x3D
}
