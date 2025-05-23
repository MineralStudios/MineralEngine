package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class TabCompletePacket(val completions: List<String>) : Packet.Outgoing {
    override fun serialize(os: ByteBuf) {
        os.writeVarInt(completions.size)
        for (completion in completions) os.writeString(completion)
    }

    override val id: Byte
        get() = 0x3A
}
