package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class BlockActionPacket(
    val x: Int,
    val y: Short,
    val z: Int,
    val blockType: Int,
    val byte1: UByte,
    val byte2: UByte
) : Packet.Outgoing {
    @OptIn(ExperimentalUnsignedTypes::class)
    override fun serialize(os: ByteBuf) {
        os.writeInt(x)
        os.writeShort(y.toInt())
        os.writeInt(z)
        os.writeByte(byte1, byte2)
        os.writeVarInt(blockType)
    }

    override val id: Byte
        get() = 0x24
}
