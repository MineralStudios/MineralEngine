package gg.mineral.api

import gg.mineral.api.command.CommandExecutor
import gg.mineral.api.command.CommandMap
import gg.mineral.api.entity.living.human.Player
import gg.mineral.api.network.connection.Connection
import gg.mineral.api.tick.TickLoop
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.ScheduledExecutorService

interface MinecraftServer : Executor, TickLoop, CommandExecutor {
    /**
     * Gets the map of registered commands.
     *
     * @return The map of registered commands.
     */
    val registeredCommands: CommandMap

    /**
     * Gets the executor.
     *
     * @return The executor.
     */
    val executor: ScheduledExecutorService

    /**
     * Gets the async executor.
     *
     * @return The async executor.
     */
    val asyncExecutor: ExecutorService

    /**
     * Starts the server.
     *
     * @throws IllegalStateException If the server is already running.
     */
    fun start(networkThreads: Int = 1)

    /**
     * Broadcasts a message to all players.
     *
     * @param message The message to broadcast.
     */
    fun broadcastMessage(message: String)

    /**
     * Gets the connections.
     *
     * @return The connections.
     */
    val connections: MutableSet<Connection>

    /**
     * Gets the player with the specified name.
     *
     * @param name The name of the player.
     *
     * @return The player with the specified name.
     */
    fun getPlayer(name: String): Player?

    /**
     * Gets the player with the specified entity ID.
     *
     * @param entityId The entity ID of the player.
     *
     * @return The player with the specified entity ID.
     */
    fun getPlayer(entityId: Int): Player?

    /**
     * Gets all the online players.
     *
     * @return All the online players.
     */
    fun getOnlinePlayers(): Collection<Player>

    /**
     * Stops the server.
     */
    fun shutdown()
}