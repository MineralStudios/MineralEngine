package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class SpawnObjectPacket(
    val entityId: Int,
    val type: Byte,
    val x: Int,
    val y: Int,
    val z: Int,
    val pitch: Byte,
    val yaw: Byte,
    val data: Int
) :
    Packet.Outgoing {
    override fun serialize(os: ByteBuf) {
        os.writeVarInt(entityId)
        os.writeByte(type.toInt())
        os.writeInt(x, y, z)
        os.writeByte(pitch, yaw)
        os.writeInt(data)
    }

    override val id: Byte
        get() = 0x0E
}
