package gg.mineral.server.network.packet.login.clientbound

import com.eatthepath.uuid.FastUUID
import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf
import java.util.*

@JvmRecord
data class LoginSuccessPacket(val uuid: UUID, val username: String) : Packet.Outgoing {
    override fun serialize(os: ByteBuf) =
        os.writeString(FastUUID.toString(uuid), username)

    override val id: Byte
        get() = 0x02
}
