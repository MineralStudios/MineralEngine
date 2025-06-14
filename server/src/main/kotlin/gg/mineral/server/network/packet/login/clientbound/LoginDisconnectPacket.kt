package gg.mineral.server.network.packet.login.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf
import net.md_5.bungee.api.chat.BaseComponent

@JvmRecord
data class LoginDisconnectPacket(val components: Array<out BaseComponent>) : Packet.Outgoing {
    override fun serialize(os: ByteBuf) =
        os.writeString(VersionedComponentSerializer.getDefault().toString(components))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LoginDisconnectPacket

        return components.contentEquals(other.components)
    }

    override fun hashCode(): Int {
        return components.contentHashCode()
    }

    override val id: Byte
        get() = 0x00
}
