package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class EntityRelativeMovePacket(val entityId: Int, val deltaX: Byte, val deltaY: Byte, val deltaZ: Byte) :
    Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        os.writeInt(entityId)
        os.writeByte(deltaX, deltaY, deltaZ)
    }

    override val id: Byte
        get() = 0x15
}
