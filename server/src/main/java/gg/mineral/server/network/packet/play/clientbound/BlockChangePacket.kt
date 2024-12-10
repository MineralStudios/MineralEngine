package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class BlockChangePacket(val x: Int, val y: Short, val z: Int, val blockId: Int, val blockMetadata: Short) :
    Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        os.writeInt(x)
        os.writeByte(y.toInt())
        os.writeInt(z)
        writeVarInt(os, blockId)
        os.writeByte(blockMetadata.toInt())
    }

    override fun getId(): Byte {
        return 0x23
    }
}
