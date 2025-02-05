package gg.mineral.server.network.packet.login.serverbound

import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.Packet
import gg.mineral.server.network.connection.ConnectionImpl
import io.netty.buffer.ByteBuf
import java.util.*

class LoginStartPacket(var name: String? = null, var uuid: UUID? = null) : Packet.Incoming, Packet.SyncHandler {
    override suspend fun receivedSync(connection: Connection) {
        if (connection is ConnectionImpl) connection.attemptLogin(name ?: "", uuid)
    }

    override fun deserialize(`is`: ByteBuf) {
        this.name = `is`.readString()
        if (`is`.readableBytes() == 0) return
        this.uuid = `is`.readUuid()
    }
}
