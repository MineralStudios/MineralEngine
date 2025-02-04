package gg.mineral.server.network.packet.play.serverbound

import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

class UseEntityPacket(var target: Int = 0, var mouse: Byte = 0) : Packet.Incoming, Packet.SyncHandler {
    override suspend fun receivedSync(connection: Connection) {
        if (mouse.toInt() == 1) { // left click
            if (target != -1) connection.player?.attack(target)
        }
    }

    override fun deserialize(`is`: ByteBuf) {
        target = `is`.readInt()
        mouse = `is`.readByte()
    }
}
