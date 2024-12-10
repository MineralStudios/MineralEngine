package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.entity.attribute.Attribute
import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class EntityPropertiesPacket(val entityId: Int, val properties: Map<String, Attribute.Property>) :
    Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        os.writeInt(entityId)
        os.writeInt(properties.size)
        writeProperties(os, properties)
    }

    override val id: Byte
        get() = 0x20
}
