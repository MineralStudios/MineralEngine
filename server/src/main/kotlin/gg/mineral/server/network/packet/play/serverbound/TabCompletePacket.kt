package gg.mineral.server.network.packet.play.serverbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

class TabCompletePacket(var text: String? = null) : Packet.Incoming {
    override fun deserialize(`is`: ByteBuf) {
        this.text = `is`.readString()
    }
}
