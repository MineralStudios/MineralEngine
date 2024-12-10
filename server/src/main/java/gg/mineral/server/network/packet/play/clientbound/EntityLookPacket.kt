package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class EntityLookPacket(val entityId: Int, val yaw: Byte, val pitch: Byte) : Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        os.writeInt(entityId)
        os.writeByte(yaw.toInt())
        os.writeByte(pitch.toInt())
    }

    override fun getId(): Byte {
        return 0x16
    }
}
