package gg.mineral.api

import gg.mineral.api.command.CommandMap
import gg.mineral.api.entity.living.human.Player
import gg.mineral.api.network.connection.Connection
import gg.mineral.api.tick.TickLoop
import java.util.concurrent.ExecutorService
import java.util.concurrent.ScheduledExecutorService

interface MinecraftServer {
    /**
     * Gets the map of registered commands.
     *
     * @return The map of registered commands.
     */
    val registeredCommands: CommandMap?

    /**
     * Gets the tick loop.
     *
     * @return The tick loop.
     */
    val tickLoop: TickLoop?

    /**
     * Gets the connections.
     *
     * @return The connections.
     */
    val connections: Set<Connection?>?

    /**
     * Gets the tick executor.
     *
     * @return The tick executor.
     */
    val tickExecutor: ScheduledExecutorService?

    /**
     * Gets the async executor.
     *
     * @return The async executor.
     */
    val asyncExecutor: ExecutorService?

    /**
     * Starts the server.
     *
     * @throws IllegalStateException If the server is already running.
     */
    fun start()

    /**
     * Stops the server.
     */
    fun shutdown()

    /**
     * Gets all the online players.
     *
     * @return All the online players.
     */
    val onlinePlayers: Collection<Player?>?

    /**
     * Gets the player with the specified name.
     *
     * @param name The name of the player.
     *
     * @return The player with the specified name.
     */
    fun getPlayer(name: String?): Player?

    /**
     * Gets the player with the specified entity ID.
     *
     * @param entityId The entity ID of the player.
     *
     * @return The player with the specified entity ID.
     */
    fun getPlayer(entityId: Int): Player?
}