package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class ParticlePacket(
    val particleName: String, val x: Float, val y: Float, val z: Float, val offsetX: Float, val offsetY: Float,
    val offsetZ: Float, val particleData: Float, val numberOfParticles: Int
) : Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        writeString(os, particleName)
        os.writeFloat(x)
        os.writeFloat(y)
        os.writeFloat(z)
        os.writeFloat(offsetX)
        os.writeFloat(offsetY)
        os.writeFloat(offsetZ)
        os.writeFloat(particleData)
        os.writeInt(numberOfParticles)
    }

    override fun getId(): Byte {
        return 0x2A
    }
}
