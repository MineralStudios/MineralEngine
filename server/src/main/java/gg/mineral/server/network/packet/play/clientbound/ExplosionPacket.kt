package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import gg.mineral.server.world.explosion.ExplosionRecord
import io.netty.buffer.ByteBuf

@JvmRecord
data class ExplosionPacket(
    val x: Float, val y: Float, val z: Float, val radius: Float, val records: List<ExplosionRecord>,
    val playerMotionX: Float,
    val playerMotionY: Float, val playerMotionZ: Float
) : Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        os.writeFloat(x)
        os.writeFloat(y)
        os.writeFloat(z)
        os.writeFloat(radius)
        os.writeInt(records.size)

        for (record in records) {
            os.writeByte(record.x.toInt())
            os.writeByte(record.y.toInt())
            os.writeByte(record.z.toInt())
        }

        os.writeFloat(playerMotionX)
        os.writeFloat(playerMotionY)
        os.writeFloat(playerMotionZ)
    }

    override fun getId(): Byte {
        return 0x27
    }
}
