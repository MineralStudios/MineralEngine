package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class MultiBlockChangePacket(val chunkX: Int, val chunkZ: Int, val records: List<BlockChangePacket>) :
    Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        os.writeInt(chunkX, chunkZ)
        os.writeShort(records.size)
        os.writeInt(records.size * 4)

        for ((x, y, z, blockId, blockMetadata) in records) {
            // XZYYTTTM
            val value = (blockMetadata.toInt() and 0xF) or
                    ((blockId and 0xFFF) shl 4) or
                    ((y.toInt() and 0xFF) shl 16) or
                    ((z and 0xF) shl 24) or
                    ((x and 0xF) shl 28)
            os.writeInt(value)
        }
    }

    override val id: Byte
        get() = 0x22
}
