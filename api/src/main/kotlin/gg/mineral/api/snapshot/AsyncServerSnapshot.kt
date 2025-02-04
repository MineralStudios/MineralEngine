package gg.mineral.api.snapshot

import gg.mineral.api.entity.living.human.Player
import gg.mineral.api.network.connection.Connection
import java.util.concurrent.Executor

interface AsyncServerSnapshot : Executor, ServerSnapshot {
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
    suspend fun getPlayer(name: String): Player?

    /**
     * Gets the player with the specified entity ID.
     *
     * @param entityId The entity ID of the player.
     *
     * @return The player with the specified entity ID.
     */
    suspend fun getPlayer(entityId: Int): Player?
}