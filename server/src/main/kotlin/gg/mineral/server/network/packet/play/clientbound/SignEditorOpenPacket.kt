package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class SignEditorOpenPacket(val x: Int, val y: Int, val z: Int) : Packet.OUTGOING {
    override fun serialize(os: ByteBuf) =
        os.writeInt(x, y, z)

    override val id: Byte
        get() = 0x36
}
