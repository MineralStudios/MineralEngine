package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class UpdateBlockEntityPacket(val x: Int, val y: Short, val z: Int, val action: Short, val nbtData: ByteArray) :
    Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        os.writeInt(x)
        os.writeShort(y.toInt())
        os.writeInt(z)
        os.writeByte(action.toInt())
        os.writeShort(nbtData.size)
        os.writeBytes(nbtData)
    }

    override fun getId(): Byte {
        return 0x35
    }
}
