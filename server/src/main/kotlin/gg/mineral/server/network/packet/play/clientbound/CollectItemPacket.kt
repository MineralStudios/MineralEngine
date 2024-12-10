package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class CollectItemPacket(val collectorEntityId: Int, val collectedEntityId: Int) : Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        os.writeInt(collectedEntityId)
        os.writeInt(collectorEntityId)
    }

    override val id: Byte
        get() = 0x0D
}
