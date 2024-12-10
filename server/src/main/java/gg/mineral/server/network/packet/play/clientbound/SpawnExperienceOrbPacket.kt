package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class SpawnExperienceOrbPacket(val entityId: Int, val x: Int, val y: Int, val z: Int, val count: Short) :
    Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        writeVarInt(os, entityId)
        os.writeInt(x)
        os.writeInt(y)
        os.writeInt(z)
        os.writeShort(count.toInt())
    }

    override fun getId(): Byte {
        return 0x11
    }
}
