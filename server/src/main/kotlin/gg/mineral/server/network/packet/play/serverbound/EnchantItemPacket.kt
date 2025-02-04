package gg.mineral.server.network.packet.play.serverbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

class EnchantItemPacket(var windowId: Byte = 0, var enchantment: Byte = 0) : Packet.Incoming {
    override fun deserialize(`is`: ByteBuf) {
        windowId = `is`.readByte()
        enchantment = `is`.readByte()
    }
}
