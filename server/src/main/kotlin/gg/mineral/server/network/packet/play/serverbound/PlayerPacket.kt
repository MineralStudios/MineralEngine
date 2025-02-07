package gg.mineral.server.network.packet.play.serverbound

import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.Packet
import gg.mineral.server.network.connection.ConnectionImpl
import io.netty.buffer.ByteBuf

open class PlayerPacket(var onGround: Boolean = false) : Packet.Incoming, Packet.SyncHandler {
    override fun receivedSync(connection: Connection) {
        connection.player?.onGround = onGround
        if (connection is ConnectionImpl) connection.clientSideActive = true
    }

    override fun deserialize(`is`: ByteBuf) {
        onGround = `is`.readBoolean()
    }
}
