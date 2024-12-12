package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class ParticlePacket(
    val particleName: String, val x: Float, val y: Float, val z: Float, val offsetX: Float, val offsetY: Float,
    val offsetZ: Float, val particleData: Float, val numberOfParticles: Int
) : Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        os.writeString(particleName)
        os.writeFloat(x, y, z, offsetX, offsetY, offsetZ, particleData)
        os.writeInt(numberOfParticles)
    }

    override val id: Byte
        get() = 0x2A
}
