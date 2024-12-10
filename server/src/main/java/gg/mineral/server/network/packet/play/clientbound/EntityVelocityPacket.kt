package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class EntityVelocityPacket(val entityId: Int, val x: Short, val y: Short, val z: Short) : Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        os.writeInt(entityId)
        os.writeShort(x.toInt())
        os.writeShort(y.toInt())
        os.writeShort(z.toInt())
    }

    override fun getId(): Byte {
        return 0x12
    }
}
