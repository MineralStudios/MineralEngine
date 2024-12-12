package gg.mineral.server.network.packet.login.clientbound

import dev.zerite.craftlib.chat.component.BaseChatComponent
import dev.zerite.craftlib.chat.component.json
import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class LoginDisconnectPacket(val baseChatComponent: BaseChatComponent) : Packet.OUTGOING {
    override fun serialize(os: ByteBuf) =
        os.writeString(baseChatComponent.json)

    override val id: Byte
        get() = 0x00
}
