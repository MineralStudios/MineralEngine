package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class SetExperiencePacket(val experienceBar: Float, val level: Short, val totalExperience: Short) :
    Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        os.writeFloat(experienceBar)
        os.writeShort(level.toInt())
        os.writeShort(totalExperience.toInt())
    }

    override fun getId(): Byte {
        return 0x1F
    }
}
