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
) : Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        writeVarInt(os, entityId)
        os.writeByte(type.toInt())
        os.writeInt(x)
        os.writeInt(y)
        os.writeInt(z)
        os.writeByte(yaw.toInt())
        os.writeByte(pitch.toInt())
        os.writeByte(headPitch.toInt())
        os.writeShort(velocityX.toInt())
        os.writeShort(velocityY.toInt())
        os.writeShort(velocityZ.toInt())
        writeMetadata(os, entries)
    }

    override fun getId(): Byte {
        return 0x0F
    }
}
