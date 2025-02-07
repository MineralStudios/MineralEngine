package gg.mineral.server.network.packet.status.serverbound

import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.Packet
import gg.mineral.server.MinecraftServerImpl
import gg.mineral.server.config.GroovyConfig
import gg.mineral.server.network.packet.status.clientbound.ResponsePacket
import gg.mineral.server.network.ping.ServerPing
import io.netty.buffer.ByteBuf

class RequestPacket : Packet.Incoming, Packet.EventLoopHandler {
    companion object {
        val serverIcon by lazy { ServerPing.Icon("server-icon.png") }
    }

    override fun receivedEventLoop(connection: Connection) {
        val server = connection.server

        if (server !is MinecraftServerImpl) return

        val config: GroovyConfig = server.config

        val serverPing = synchronized(server.players) {
            ServerPing(
                config.motd,
                server.players.size,
                config.maxPlayers, 5,
                config.brandName, serverIcon
            )
        }

        connection.queuePacket(ResponsePacket(serverPing.toJsonString()))
    }

    override fun deserialize(`is`: ByteBuf) {}
}
