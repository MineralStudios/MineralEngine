package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class PlayerPositionAndLookPacket(
    val x: Double, val headY: Double, val z: Double, val yaw: Float, val pitch: Float,
    val onGround: Boolean
) : Packet.Outgoing {
    override fun serialize(os: ByteBuf) {
        os.writeDouble(x)
        os.writeDouble(headY)
        os.writeDouble(z)
        os.writeFloat(yaw, pitch)
        os.writeBoolean(onGround)
    }

    override val id: Byte
        get() = 0x08
}
