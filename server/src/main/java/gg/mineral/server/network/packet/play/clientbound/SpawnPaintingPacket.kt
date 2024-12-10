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
        writeVarInt(os, entityId)
        writeString(os, title)
        os.writeInt(x)
        os.writeInt(y)
        os.writeInt(z)
        os.writeInt(direction)
    }

    override fun getId(): Byte {
        return 0x10
    }
}
