package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class EntityEffectPacket(val entityId: Int, val effectId: Byte, val amplifier: Byte, val duration: Short) :
    Packet.Outgoing {
    override fun serialize(os: ByteBuf) {
        os.writeInt(entityId)
        os.writeByte(effectId.toInt())
        os.writeByte(amplifier.toInt())
        os.writeShort(duration.toInt())
    }

    override val id: Byte
        get() = 0x1D
}
