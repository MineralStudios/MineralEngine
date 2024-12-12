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
        os.writeFloat(x, y, z, radius)
        os.writeInt(records.size)

        for (record in records)
            os.writeByte(record.x, record.y, record.z)

        os.writeFloat(playerMotionX, playerMotionY, playerMotionZ)
    }

    override val id: Byte
        get() = 0x27
}
