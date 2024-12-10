package gg.mineral.server.network.packet.play.clientbound

import dev.zerite.craftlib.chat.component.BaseChatComponent
import dev.zerite.craftlib.chat.component.json
import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class DisconnectPacket(val baseChatComponent: BaseChatComponent) : Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        writeString(os, baseChatComponent.json)
    }

    override fun getId(): Byte {
        return 0x40
    }
}
