package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class RemoveEntityEffectPacket(val entityId: Int, val effectId: Byte) : Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        os.writeInt(entityId)
        os.writeByte(effectId.toInt())
    }

    override fun getId(): Byte {
        return 0x1E
    }
}
