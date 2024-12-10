package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf
import it.unimi.dsi.fastutil.objects.Object2IntMap

@JvmRecord
data class StatisticsPacket(val statistics: Object2IntMap<String>) : Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        os.writeVarInt(statistics.size)

        for (entry in statistics.object2IntEntrySet()) {
            writeString(os, entry.key)
            os.writeVarInt(entry.intValue)
        }
    }

    override val id: Byte
        get() = 0x37
}
