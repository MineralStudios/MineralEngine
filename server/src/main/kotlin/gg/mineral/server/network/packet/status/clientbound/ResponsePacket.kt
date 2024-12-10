package gg.mineral.server.network.packet.status.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class ResponsePacket(val jsonResponse: String) : Packet.OUTGOING {
    override fun serialize(os: ByteBuf) =
        writeString(os, jsonResponse)

    override val id: Byte
        get() = 0x00
}
