package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class SpawnExperienceOrbPacket(val entityId: Int, val x: Int, val y: Int, val z: Int, val count: Short) :
    Packet.Outgoing {
    override fun serialize(os: ByteBuf) {
        os.writeVarInt(entityId)
        os.writeInt(x, y, z)
        os.writeShort(count.toInt())
    }

    override val id: Byte
        get() = 0x11
}
