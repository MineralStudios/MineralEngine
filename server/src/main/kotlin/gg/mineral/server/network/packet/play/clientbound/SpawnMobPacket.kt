package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.entity.meta.EntityMetadata
import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class SpawnMobPacket(
    val entityId: Int,
    val type: Short,
    val x: Int,
    val y: Int,
    val z: Int,
    val yaw: Byte,
    val pitch: Byte,
    val headPitch: Byte,
    val velocityX: Short,
    val velocityY: Short,
    val velocityZ: Short,
    val entries: List<EntityMetadata.Entry>
) : Packet.Outgoing {
    override fun serialize(os: ByteBuf) {
        os.writeVarInt(entityId)
        os.writeByte(type.toInt())
        os.writeInt(x, y, z)
        os.writeByte(yaw, pitch, headPitch)
        os.writeShort(velocityX, velocityY, velocityZ)
        os.writeMetadata(entries)
    }

    override val id: Byte
        get() = 0x0F
}
