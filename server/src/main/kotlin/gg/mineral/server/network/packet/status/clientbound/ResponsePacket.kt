package gg.mineral.server.network.packet.status.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

data class ResponsePacket(val jsonResponse: String) : Packet.Outgoing {
    override fun serialize(os: ByteBuf) =
        os.writeString(jsonResponse)

    override val id: Byte
        get() = 0x00
}
