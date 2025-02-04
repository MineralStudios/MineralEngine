package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class TimeUpdatePacket(val ageOfWorld: Long, val timeOfDay: Long) : Packet.Outgoing {
    override fun serialize(os: ByteBuf) {
        os.writeLong(ageOfWorld)
        os.writeLong(timeOfDay)
    }

    override val id: Byte
        get() = 0x03
}
