package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class UpdateBlockEntityPacket(val x: Int, val y: Short, val z: Int, val action: Short, val nbtData: ByteArray) :
    Packet.Outgoing {
    override fun serialize(os: ByteBuf) {
        os.writeInt(x)
        os.writeShort(y.toInt())
        os.writeInt(z)
        os.writeByte(action.toInt())
        os.writeShort(nbtData.size)
        os.writeBytes(nbtData)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UpdateBlockEntityPacket

        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false
        if (action != other.action) return false
        if (!nbtData.contentEquals(other.nbtData)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        result = 31 * result + z
        result = 31 * result + action
        result = 31 * result + nbtData.contentHashCode()
        return result
    }

    override val id: Byte
        get() = 0x35
}
