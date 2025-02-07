package gg.mineral.server.network.packet.login.serverbound

import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.Packet
import gg.mineral.server.network.connection.ConnectionImpl
import io.netty.buffer.ByteBuf

class LoginStartPacket(var name: String = "") : Packet.Incoming, Packet.SyncHandler {
    override fun receivedSync(connection: Connection) {
        if (connection is ConnectionImpl) connection.attemptLogin(name)
    }

    override fun deserialize(`is`: ByteBuf) {
        this.name = `is`.readString()
    }
}
