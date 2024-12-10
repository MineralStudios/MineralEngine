package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class EntityHeadLookPacket(val entityId: Int, val headYaw: Byte) : Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        os.writeInt(entityId)
        os.writeByte(headYaw.toInt())
    }

    override val id: Byte
        get() = 0x19
}
