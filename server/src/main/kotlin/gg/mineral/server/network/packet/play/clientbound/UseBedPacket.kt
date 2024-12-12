package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class UseBedPacket(val entityId: Int, val x: Int, val y: Short, val z: Int) : Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        os.writeInt(entityId, x)
        os.writeByte(y.toInt())
        os.writeInt(z)
    }

    override val id: Byte
        get() = 0x0A
}
