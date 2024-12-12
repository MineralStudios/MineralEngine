package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class ScoreboardObjectivePacket(val objectiveName: String, val objectiveValue: String, val createOrRemove: Byte) :
    Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        os.writeString(objectiveName, objectiveValue)
        os.writeByte(createOrRemove.toInt())
    }

    override val id: Byte
        get() = 0x3B
}
