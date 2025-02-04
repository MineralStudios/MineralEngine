package gg.mineral.api.snapshot

import gg.mineral.api.MinecraftServer
import gg.mineral.api.entity.living.human.Player

interface ServerSnapshot {
    /**
     * Gets all the online players.
     *
     * @return All the online players.
     */
    suspend fun getOnlinePlayers(): Collection<Player>

    /**
     * Gets the number of online players.
     *
     * @return The number of online players.
     */
    suspend fun getOnlineCount(): Int

    /**
     * Gets the server.
     *
     * @return The server.
     */
    val server: MinecraftServer

    /**
     * Gets the server name.
     *
     * @return The server name.
     */
    val name: String
}