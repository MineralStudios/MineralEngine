package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class SpawnGlobalEntityPacket(val entityId: Int, val type: Byte, val x: Int, val y: Int, val z: Int) :
    Packet.Outgoing {
    override fun serialize(os: ByteBuf) {
        os.writeVarInt(entityId)
        os.writeByte(type.toInt())
        os.writeInt(x, y, z)
    }

    override val id: Byte
        get() = 0x2C
}
