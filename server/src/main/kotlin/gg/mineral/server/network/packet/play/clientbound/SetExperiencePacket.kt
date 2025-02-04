package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class SetExperiencePacket(val experienceBar: Float, val level: Short, val totalExperience: Short) :
    Packet.Outgoing {
    override fun serialize(os: ByteBuf) {
        os.writeFloat(experienceBar)
        os.writeShort(level, totalExperience)
    }

    override val id: Byte
        get() = 0x1F
}
