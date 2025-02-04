package gg.mineral.server.network.packet.play.bidirectional

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf


class PluginMessagePacket(var channel: String? = null, var data: ByteArray? = null) : Packet.Incoming, Packet.Outgoing {
    override fun serialize(os: ByteBuf) {
        val dataSize = data?.size ?: 0

        if (dataSize == 0)
            return

        channel?.let { os.writeString(it) }
        os.writeShort(dataSize)
        os.writeBytes(data)
    }

    override fun deserialize(`is`: ByteBuf) {
        channel = `is`.readString()
        val length = `is`.readShort()
        data = ByteArray(length.toInt())
        `is`.readBytes(data)
    }

    override val id: Byte
        get() = 0x3F
}
