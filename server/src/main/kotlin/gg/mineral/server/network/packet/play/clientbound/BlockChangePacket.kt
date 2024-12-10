package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class BlockChangePacket(
    @JvmField val x: Int,
    @JvmField val y: Short,
    @JvmField val z: Int,
    @JvmField val blockId: Int,
    @JvmField val blockMetadata: Short
) :
    Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        os.writeInt(x)
        os.writeByte(y.toInt())
        os.writeInt(z)
        os.writeVarInt(blockId)
        os.writeByte(blockMetadata.toInt())
    }

    override val id: Byte
        get() = 0x23
}
