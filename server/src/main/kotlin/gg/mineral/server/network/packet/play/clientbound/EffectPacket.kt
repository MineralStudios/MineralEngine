package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class EffectPacket(
    val effectId: Int,
    val x: Int,
    val y: UByte,
    val z: Int,
    val data: Int,
    val disableRelativeVolume: Boolean
) : Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        os.writeInt(effectId, x)
        os.writeByte(y.toInt())
        os.writeInt(z, data)
        os.writeBoolean(disableRelativeVolume)
    }

    override val id: Byte
        get() = 0x28
}
