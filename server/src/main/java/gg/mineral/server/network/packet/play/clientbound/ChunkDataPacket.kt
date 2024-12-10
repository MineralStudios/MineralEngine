package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class ChunkDataPacket(
    val chunkX: Int, val chunkZ: Int, val groundUpContinuous: Boolean, val primaryBitMap: Int,
    val addBitMap: Int,
    val compressedData: ByteArray
) : Packet.OUTGOING {
    constructor(chunkX: Int, chunkZ: Int) : this(chunkX, chunkZ, true, 0, 0, ByteArray(0))

    override fun serialize(os: ByteBuf) {
        os.writeInt(chunkX)
        os.writeInt(chunkZ)
        os.writeBoolean(groundUpContinuous)
        os.writeShort(primaryBitMap)
        os.writeShort(addBitMap)
        os.writeInt(compressedData.size)
        os.writeBytes(compressedData)
    }

    override fun getId(): Byte {
        return 0x21
    }
}
