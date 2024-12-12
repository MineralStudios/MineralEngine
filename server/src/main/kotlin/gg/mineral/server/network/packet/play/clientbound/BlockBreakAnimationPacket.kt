package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class BlockBreakAnimationPacket(val entityId: Int, val x: Int, val y: Int, val z: Int, val destroyStage: Byte) :
    Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        os.writeVarInt(entityId)
        os.writeInt(x, y, z)
        os.writeByte(destroyStage.toInt())
    }

    override val id: Byte
        get() = 0x25
}
