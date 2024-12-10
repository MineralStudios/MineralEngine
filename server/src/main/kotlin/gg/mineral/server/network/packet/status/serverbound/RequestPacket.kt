package gg.mineral.server.network.packet.status.serverbound

import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.Packet
import gg.mineral.server.MinecraftServerImpl
import gg.mineral.server.config.GroovyConfig
import gg.mineral.server.network.packet.status.clientbound.ResponsePacket
import gg.mineral.server.network.ping.ServerPing
import io.netty.buffer.ByteBuf

class RequestPacket : Packet.INCOMING {
    override fun received(connection: Connection) {
        val server = connection.server
        if (server is MinecraftServerImpl) {
            val config: GroovyConfig = server.config

            val serverPing = ServerPing(
                config.motd,
                connection.server.onlinePlayers.size as Int,
                config.maxPlayers, 5,
                config.brandName, ServerPing.Icon("server-icon.png")
            )
            connection.queuePacket(ResponsePacket(serverPing.toJsonString()))
        }
    }

    override fun deserialize(`is`: ByteBuf) {
    }
}
