package gg.mineral.server.network.packet.play.serverbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

class ClientStatusPacket(var actionId: Byte = 0) : Packet.Incoming {
    override fun deserialize(`is`: ByteBuf) {
        actionId = `is`.readByte()
    }
}
