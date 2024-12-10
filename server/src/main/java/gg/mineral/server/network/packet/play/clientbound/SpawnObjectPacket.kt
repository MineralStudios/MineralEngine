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
    Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        writeVarInt(os, entityId)
        os.writeByte(type.toInt())
        os.writeInt(x)
        os.writeInt(y)
        os.writeInt(z)
        os.writeByte(pitch.toInt())
        os.writeByte(yaw.toInt())
        os.writeInt(data)
    }

    override fun getId(): Byte {
        return 0x0E
    }
}
