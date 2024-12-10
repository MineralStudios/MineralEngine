package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.entity.living.human.property.Gamemode
import gg.mineral.api.network.packet.Packet
import gg.mineral.api.world.property.Difficulty
import gg.mineral.api.world.property.Dimension
import gg.mineral.api.world.property.LevelType
import io.netty.buffer.ByteBuf

@JvmRecord
data class JoinGamePacket(
    val entityId: Int, val gamemode: Gamemode, val dimension: Dimension, val difficulty: Difficulty,
    val maxPlayers: Short, val levelType: LevelType
) : Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        os.writeInt(entityId)
        os.writeByte(gamemode.id.toInt())
        os.writeByte(dimension.id.toInt())
        os.writeByte(difficulty.id.toInt())
        os.writeByte(maxPlayers.toInt())
        writeString(os, levelType.string())
    }

    override fun getId(): Byte {
        return 0x01
    }
}
