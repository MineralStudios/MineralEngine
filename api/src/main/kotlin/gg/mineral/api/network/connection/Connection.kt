package gg.mineral.api.network.connection

import gg.mineral.api.MinecraftServer
import gg.mineral.api.entity.living.human.Player
import gg.mineral.api.network.packet.Packet
import net.md_5.bungee.api.chat.BaseComponent
import java.util.*

interface Connection : PreLoginConnection {
    /**
     * Disconnects the connection.
     *
     * @param components The components to send to the client.
     */
    fun disconnect(vararg components: BaseComponent)

    /**
     * Gets the player attached to the connection.
     *
     * @return The player attached to the connection.
     */
    val player: Player?

    /**
     * Gets the UUID of the player attached to the connection.
     *
     * @return The UUID of the player attached to the connection.
     */
    val uuid: UUID?

    /**
     * Gets the server.
     *
     * @return The server.
     */
    val server: MinecraftServer

    /**
     * Queues packets to be sent to the client.
     *
     * @param packets
     */
    fun queuePacket(vararg packets: Packet.Outgoing)

    /**
     * Gets the current ping of the connection.
     *
     * @return The current ping in milliseconds.
     */
    var ping: Int
}
