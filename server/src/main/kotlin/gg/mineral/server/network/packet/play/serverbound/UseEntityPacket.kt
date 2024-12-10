package gg.mineral.server.network.packet.play.serverbound

import gg.mineral.api.entity.living.human.Player
import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

class UseEntityPacket(var target: Int = 0, var mouse: Byte = 0) : Packet.INCOMING {
    override fun received(connection: Connection) {
        if (mouse.toInt() == 1) { // left click
            if (target != -1) {
                val attacker: Player? = connection.player

                attacker?.attack(target)
            }
        }
    }

    override fun deserialize(`is`: ByteBuf) {
        target = `is`.readInt()
        mouse = `is`.readByte()
    }
}
