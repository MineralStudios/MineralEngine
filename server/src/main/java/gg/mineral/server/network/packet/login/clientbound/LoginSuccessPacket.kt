package gg.mineral.server.network.packet.login.clientbound

import com.eatthepath.uuid.FastUUID
import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf
import java.util.*

@JvmRecord
data class LoginSuccessPacket(val uuid: UUID, val username: String) : Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        writeString(os, FastUUID.toString(uuid), username)
    }

    override fun getId(): Byte {
        return 0x02
    }
}
