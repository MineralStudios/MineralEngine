package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class MapsPacket(val itemDamage: Int, val data: ByteArray) : Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        os.writeVarInt(itemDamage)
        os.writeShort(data.size)
        os.writeBytes(data)
    }

    override val id: Byte
        get() = 0x34

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MapsPacket

        if (itemDamage != other.itemDamage) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = itemDamage
        result = 31 * result + data.contentHashCode()
        return result
    }
}
