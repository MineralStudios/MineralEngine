package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class SpawnPositionPacket(val x: Int, val headY: Int, val z: Int) : Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        os.writeInt(x)
        os.writeInt(headY)
        os.writeInt(z)
    }

    override val id: Byte
        get() = 0x05
}
