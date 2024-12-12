package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class SpawnPaintingPacket(
    val entityId: Int,
    val title: String,
    val x: Int,
    val y: Int,
    val z: Int,
    val direction: Int
) : Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        os.writeVarInt(entityId)
        os.writeString(title)
        os.writeInt(x, y, z, direction)
    }

    override val id: Byte
        get() = 0x10
}
