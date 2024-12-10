package gg.mineral.api.entity.living.human

import gg.mineral.api.command.CommandExecutor
import gg.mineral.api.entity.living.Human
import gg.mineral.api.network.connection.Connection

interface Player : Human, CommandExecutor {
    /**
     * Get the connection of the player.
     *
     * @return The connection of the player.
     */
    val connection: Connection?
}
