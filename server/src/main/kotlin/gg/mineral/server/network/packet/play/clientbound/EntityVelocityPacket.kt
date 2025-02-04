package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class EntityVelocityPacket(val entityId: Int, val x: Short, val y: Short, val z: Short) : Packet.Outgoing {
    override fun serialize(os: ByteBuf) {
        os.writeInt(entityId)
        os.writeShort(x, y, z)
    }

    override val id: Byte
        get() = 0x12
}
