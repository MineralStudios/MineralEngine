package gg.mineral.server.network.packet.play.serverbound

import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import lombok.experimental.Accessors

@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(fluent = true)
class EntityActionPacket : Packet.INCOMING {
    private var entityId = 0
    private var actionId: Byte = 0
    private var jumpBoost = 0

    override fun received(connection: Connection) {
        val player = connection.server.getPlayer(entityId) ?: return

        when (actionId) {
            0 -> {}
            1 -> {}
            2 -> {}
            3 -> {}
            4 -> {
                player.setSprinting(true)
                player.setExtraKnockback(true)
            }

            5 -> {
                player.setSprinting(false)
                player.setExtraKnockback(false)
            }

            6 -> {}
        }
    }

    override fun deserialize(`is`: ByteBuf) {
        entityId = `is`.readInt()
        actionId = `is`.readByte()
        jumpBoost = `is`.readInt()
    }
}
