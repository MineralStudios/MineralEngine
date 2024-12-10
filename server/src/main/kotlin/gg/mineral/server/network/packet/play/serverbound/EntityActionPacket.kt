package gg.mineral.server.network.packet.play.serverbound

import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf


class EntityActionPacket(var entityId: Int = 0, var actionId: Byte = 0, var jumpBoost: Int = 0) : Packet.INCOMING {
    override fun received(connection: Connection) {
        val player = connection.server.getPlayer(entityId) ?: return

        when (actionId.toInt()) {
            0 -> {}
            1 -> {}
            2 -> {}
            3 -> {}
            4 -> {
                player.sprinting = true
                player.extraKnockback = true
            }

            5 -> {
                player.sprinting = false
                player.extraKnockback = false
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
