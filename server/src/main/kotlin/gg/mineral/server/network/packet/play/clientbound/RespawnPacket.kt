package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.entity.living.human.property.Gamemode
import gg.mineral.api.network.packet.Packet
import gg.mineral.api.world.property.Difficulty
import gg.mineral.api.world.property.Dimension
import gg.mineral.api.world.property.LevelType
import io.netty.buffer.ByteBuf

@JvmRecord
data class RespawnPacket(
    val dimension: Dimension,
    val difficulty: Difficulty,
    val gamemode: Gamemode,
    val levelType: LevelType
) :
    Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        os.writeInt(dimension.id.toInt())
        os.writeByte(difficulty.id.toInt())
        os.writeByte(gamemode.id.toInt())
        writeString(os, levelType.string())
    }

    override val id: Byte
        get() = 0x07
}
