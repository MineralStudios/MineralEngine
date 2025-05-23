package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class TeamsPacket(
    val teamName: String, val teamDisplayName: String, val teamPrefix: String, val teamSuffix: String,
    val mode: Byte,
    val friendlyFire: Byte, val players: List<String>
) : Packet.Outgoing {
    override fun serialize(os: ByteBuf) {
        os.writeString(teamName)
        os.writeByte(mode.toInt())
        os.writeString(teamDisplayName, teamPrefix, teamSuffix)
        os.writeByte(friendlyFire.toInt())
        os.writeShort(players.size)
        os.writeString(players)
    }

    override val id: Byte
        get() = 0x3E
}
