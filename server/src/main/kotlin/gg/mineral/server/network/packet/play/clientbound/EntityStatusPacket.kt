package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class EntityStatusPacket(val entityId: Int, val entityStatus: Byte) : Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        os.writeInt(entityId)
        os.writeByte(entityStatus.toInt())
    }

    override val id: Byte
        get() = 0x1A
}
