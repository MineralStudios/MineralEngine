package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class PlayerListItemPacket(val playerName: String, val online: Boolean, val ping: Short) : Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        writeString(os, playerName)
        os.writeBoolean(online)
        os.writeShort(ping.toInt())
    }

    override val id: Byte
        get() = 0x38
}
