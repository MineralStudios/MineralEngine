package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class EntityLookAndRelativeMovePacket(
    val entityId: Int, val deltaX: Byte, val deltaY: Byte, val deltaZ: Byte, val yaw: Byte,
    val pitch: Byte
) : Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        os.writeInt(entityId)
        os.writeByte(deltaX.toInt())
        os.writeByte(deltaY.toInt())
        os.writeByte(deltaZ.toInt())
        os.writeByte(yaw.toInt())
        os.writeByte(pitch.toInt())
    }

    override val id: Byte
        get() = 0x17
}
