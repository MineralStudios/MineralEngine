package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import gg.mineral.api.world.chunk.Chunk
import gg.mineral.server.world.chunk.ChunkImpl
import gg.mineral.server.world.chunk.ChunkImpl.Companion.compress
import io.netty.buffer.ByteBuf

@JvmRecord
data class MapChunkBulkPacket(val skyLight: Boolean, val chunks: List<Chunk>) :
    Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        val amount = chunks.size
        val bytes = ByteArray(196864 * amount)
        var bytesPosition = 0

        val output = arrayOfNulls<Pair>(amount)
        for (i in 0..<amount) {
            val chunk = chunks[i]
            if (chunk is ChunkImpl) {
                val out: ChunkDataPacket = chunk.toPacket(skyLight, false)
                System.arraycopy(out.compressedData, 0, bytes, bytesPosition, out.compressedData.size)
                bytesPosition += out.compressedData.size
                output[i] = Pair(chunk, out)
            }
        }

        val compressed = compress(bytes, bytesPosition)

        os.writeShort(amount)
        os.writeInt(compressed.size)
        os.writeBoolean(skyLight)
        os.writeBytes(compressed)

        for ((col, out) in output.filterNotNull()) {
            os.writeInt(col.x.toInt(), col.z.toInt())
            os.writeShort(out.primaryBitMap, out.addBitMap)
        }
    }

    override val id: Byte
        get() = 0x26

    @JvmRecord
    private data class Pair(val chunk: Chunk, val packet: ChunkDataPacket)
}
