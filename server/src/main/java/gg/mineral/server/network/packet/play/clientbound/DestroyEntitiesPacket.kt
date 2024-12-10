package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class DestroyEntitiesPacket(val entityIds: IntArray) : Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        os.writeByte(entityIds.size)
        writeIntArray(os, entityIds)
    }

    override fun getId(): Byte {
        return 0x13
    }
}
