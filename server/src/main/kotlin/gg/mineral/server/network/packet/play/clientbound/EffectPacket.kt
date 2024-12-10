package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class EffectPacket(
    val effectId: Int,
    val x: Int,
    val y: Short,
    val z: Int,
    val data: Int,
    val disableRelativeVolume: Boolean
) : Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        os.writeInt(effectId)
        os.writeInt(x)
        os.writeByte(y.toInt())
        os.writeInt(z)
        os.writeInt(data)
        os.writeBoolean(disableRelativeVolume)
    }

    override val id: Byte
        get() = 0x28
}
