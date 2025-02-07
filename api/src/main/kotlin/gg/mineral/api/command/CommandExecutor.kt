package gg.mineral.api.command

import gg.mineral.api.MinecraftServer
import net.md_5.bungee.api.chat.BaseComponent

interface CommandExecutor {
    /**
     * The permissions of the command executor.
     *
     * @return The permissions of the command executor.
     */
    val permissions: MutableSet<String>

    /**
     * Checks if the command executor has a permission.
     *
     * @param permission The permission.
     *
     * @return If the command executor has the permission.
     */
    fun hasPermission(permission: String): Boolean {
        return permissions.contains(permission)
    }

    /**
     * Adds a permission to the command executor.
     *
     * @param permission The permission.
     *
     * @return If the permission was added.
     */
    fun addPermission(permission: String): Boolean {
        return permissions.add(permission)
    }

    /**
     * Removes a permission from the command executor.
     *
     * @param permission The permission.
     *
     * @return If the permission was removed.
     */
    fun removePermission(permission: String): Boolean {
        return permissions.remove(permission)
    }

    /**
     * Sends a message to the command executor.
     *
     * @param message The message.
     */
    fun msg(message: String)

    /**
     * Sends a message to the command executor.
     *
     * @param baseComponents The message.
     */
    fun msg(vararg baseComponents: BaseComponent)

    /**
     * Gets the server.
     *
     * @return The server.
     */
    val server: MinecraftServer
}
