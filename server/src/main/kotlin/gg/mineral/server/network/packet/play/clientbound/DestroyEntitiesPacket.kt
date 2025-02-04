package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class DestroyEntitiesPacket(val entityIds: IntArray) : Packet.Outgoing {
    override fun serialize(os: ByteBuf) {
        os.writeByte(entityIds.size)
        os.writeIntArray(entityIds)
    }

    override val id: Byte
        get() = 0x13

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DestroyEntitiesPacket

        return entityIds.contentEquals(other.entityIds)
    }

    override fun hashCode(): Int = entityIds.contentHashCode()
}
