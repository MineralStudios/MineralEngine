package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class ChunkDataPacket(
    val chunkX: Int, val chunkZ: Int, val groundUpContinuous: Boolean, @JvmField val primaryBitMap: Int,
    @JvmField val addBitMap: Int,
    @JvmField val compressedData: ByteArray
) : Packet.OUTGOING {
    constructor(chunkX: Int, chunkZ: Int) : this(chunkX, chunkZ, true, 0, 0, ByteArray(0))

    override fun serialize(os: ByteBuf) {
        os.writeInt(chunkX, chunkZ)
        os.writeBoolean(groundUpContinuous)
        os.writeShort(primaryBitMap, addBitMap)
        os.writeInt(compressedData.size)
        os.writeBytes(compressedData)
    }

    override val id: Byte
        get() = 0x21

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChunkDataPacket

        if (chunkX != other.chunkX) return false
        if (chunkZ != other.chunkZ) return false
        if (groundUpContinuous != other.groundUpContinuous) return false
        if (primaryBitMap != other.primaryBitMap) return false
        if (addBitMap != other.addBitMap) return false
        if (!compressedData.contentEquals(other.compressedData)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = chunkX
        result = 31 * result + chunkZ
        result = 31 * result + groundUpContinuous.hashCode()
        result = 31 * result + primaryBitMap
        result = 31 * result + addBitMap
        result = 31 * result + compressedData.contentHashCode()
        return result
    }
}
