package gg.mineral.api

import gg.mineral.api.command.CommandMap
import gg.mineral.api.network.channel.FakeChannel
import gg.mineral.api.network.channel.MineralChannelInitializer
import gg.mineral.api.snapshot.ServerSnapshot
import java.util.concurrent.ScheduledExecutorService

interface MinecraftServer : ServerSnapshot {
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
     * Gets the server snapshots.
     *
     * @return The server snapshots.
     */
    val snapshots: Array<ServerSnapshot>

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
    suspend fun broadcastMessage(message: String)

    /**
     * Stops the server.
     */
    fun shutdown()

    /**
     * Creates a fake channel.
     *
     * @param initializer The initializer.
     *
     * @return The fake channel.
     */
    fun createFakeChannel(initializer: MineralChannelInitializer, peerChannel: FakeChannel? = null): FakeChannel

    /**
     * Creates a fake server channel.
     *
     * @param peerChannel The peer channel.
     *
     * @return The fake server channel.
     */
    fun createFakeServerChannel(peerChannel: FakeChannel? = null): FakeChannel
}