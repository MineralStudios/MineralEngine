package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class UpdateHealthPacket(val health: Float, val hunger: Short, val saturation: Float) : Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        os.writeFloat(health)
        os.writeShort(hunger.toInt())
        os.writeFloat(saturation)
    }

    override fun getId(): Byte {
        return 0x06
    }
}
