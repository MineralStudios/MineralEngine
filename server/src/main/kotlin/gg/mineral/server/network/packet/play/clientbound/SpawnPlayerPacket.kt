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
        os.writeVarInt(entityId)
        os.writeString(playerUUID, playerName)
        os.writeVarInt(playerProperties.size)

        // TODO: Make it more concise like below
        for (playerProperty in playerProperties) os.writeString(
            playerProperty.name,
            playerProperty.value,
            playerProperty.signature
        )

        os.writeInt(x, y, z)
        os.writeByte(yaw, pitch)
        os.writeShort(currentItem.toInt())
        os.writeMetadata(entries)
    }

    override val id: Byte
        get() = 0x0C
}
