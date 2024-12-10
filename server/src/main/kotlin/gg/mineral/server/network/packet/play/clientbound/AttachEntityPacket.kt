package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class AttachEntityPacket(val entityId: Int, val vehicleId: Int, val leash: Boolean) : Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        os.writeInt(entityId)
        os.writeInt(vehicleId)
        os.writeBoolean(leash)
    }

    override val id: Byte
        get() = 0x1B
}
