package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class SoundEffectPacket(
    val soundName: String,
    val x: Int,
    val y: Int,
    val z: Int,
    val volume: Float,
    val pitch: Short
) : Packet.Outgoing {
    override fun serialize(os: ByteBuf) {
        os.writeString(soundName)
        os.writeInt(x, y, z)
        os.writeFloat(volume)
        os.writeByte(pitch.toInt())
    }

    override val id: Byte
        get() = 0x29
}
