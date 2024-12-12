package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class EntityTeleportPacket(val entityId: Int, val x: Int, val y: Int, val z: Int, val yaw: Byte, val pitch: Byte) :
    Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        os.writeInt(entityId, x, y, z)
        os.writeByte(yaw, pitch)
    }

    override val id: Byte
        get() = 0x18
}
