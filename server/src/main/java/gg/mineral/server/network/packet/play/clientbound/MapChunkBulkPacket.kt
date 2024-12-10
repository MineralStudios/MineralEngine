package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import gg.mineral.api.world.chunk.Chunk
import gg.mineral.api.world.chunk.Chunk.x
import gg.mineral.api.world.chunk.Chunk.z
import gg.mineral.server.world.chunk.ChunkImpl
import gg.mineral.server.world.chunk.ChunkImpl.Companion.compress
import gg.mineral.server.world.chunk.ChunkImpl.toPacket
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
            if (chunks[i] is ChunkImpl) {
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

        for ((col, out) in output) {
            os.writeInt(col.x.toInt())
            os.writeInt(col.z.toInt())
            os.writeShort(out.primaryBitMap)
            os.writeShort(out.addBitMap)
        }
    }

    override fun getId(): Byte {
        return 0x26
    }

    @JvmRecord
    private data class Pair(val chunk: Chunk, val packet: ChunkDataPacket)
}
