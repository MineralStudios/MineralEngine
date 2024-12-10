package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.entity.living.human.property.PlayerProperty
import gg.mineral.api.entity.meta.EntityMetadata
import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class SpawnPlayerPacket(
    val entityId: Int,
    val x: Int,
    val y: Int,
    val z: Int,
    val yaw: Byte,
    val pitch: Byte, val playerUUID: String,
    val playerName: String,
    val playerProperties: List<PlayerProperty>,
    val currentItem: Short,
    val entries: List<EntityMetadata.Entry>
) : Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        writeVarInt(os, entityId)
        writeString(os, playerUUID)
        writeString(os, playerName)
        writeVarInt(os, playerProperties.size)

        // TODO: Make it more concise like below
        for (playerProperty in playerProperties) writeString(
            os,
            playerProperty.name,
            playerProperty.value,
            playerProperty.signature
        )

        os.writeInt(x)
        os.writeInt(y)
        os.writeInt(z)
        os.writeByte(yaw.toInt())
        os.writeByte(pitch.toInt())
        os.writeShort(currentItem.toInt())
        writeMetadata(os, entries)
    }

    override fun getId(): Byte {
        return 0x0C
    }
}
