package gg.mineral.api.network.connection

import dev.zerite.craftlib.chat.component.BaseChatComponent
import gg.mineral.api.MinecraftServer
import gg.mineral.api.entity.living.human.Player
import gg.mineral.api.network.packet.Packet
import java.util.*

interface Connection : PreLoginConnection {
    /**
     * Disconnects the connection.
     *
     * @param disconnectMessage
     */
    fun disconnect(disconnectMessage: BaseChatComponent?)

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
    val server: MinecraftServer?

    /**
     * Queues packets to be sent to the client.
     *
     * @param packets
     */
    fun queuePacket(vararg packets: Packet.OUTGOING?)
}
