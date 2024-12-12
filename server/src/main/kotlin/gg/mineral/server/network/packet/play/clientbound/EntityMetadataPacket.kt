package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.entity.meta.EntityMetadata
import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class EntityMetadataPacket(val entityId: Int, val entries: List<EntityMetadata.Entry>) :
    Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        os.writeInt(entityId)
        os.writeMetadata(entries)
    }

    override val id: Byte
        get() = 0x1C
}
