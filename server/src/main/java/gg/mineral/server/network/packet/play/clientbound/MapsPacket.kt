package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class MapsPacket(val itemDamage: Int, val data: ByteArray) : Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        writeVarInt(os, itemDamage)
        os.writeShort(data.size)
        os.writeBytes(data)
    }

    override fun getId(): Byte {
        return 0x34
    }
}
