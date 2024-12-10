package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class TabCompletePacket(val completions: List<String>) : Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        writeVarInt(os, completions.size)

        for (completion in completions) writeString(os, completion)
    }

    override fun getId(): Byte {
        return 0x3A
    }
}
