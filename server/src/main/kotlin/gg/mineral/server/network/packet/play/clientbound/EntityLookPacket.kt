package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class EntityLookPacket(val entityId: Int, val yaw: Byte, val pitch: Byte) : Packet.Outgoing {
    override fun serialize(os: ByteBuf) {
        os.writeInt(entityId)
        os.writeByte(yaw, pitch)
    }

    override val id: Byte
        get() = 0x16
}
