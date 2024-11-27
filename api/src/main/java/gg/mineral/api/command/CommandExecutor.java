package gg.mineral.api.command;

import java.util.Set;

import gg.mineral.api.MinecraftServer;

public interface CommandExecutor {

    /**
     * The permissions of the command executor.
     * 
     * @return The permissions of the command executor.
     */
    Set<String> getPermissions();

    /**
     * Checks if the command executor has a permission.
     * 
     * @param permission The permission.
     * 
     * @return If the command executor has the permission.
     */
    default boolean hasPermission(String permission) {
        return getPermissions().contains(permission);
    }

    /**
     * Adds a permission to the command executor.
     * 
     * @param permission The permission.
     * 
     * @return If the permission was added.
     */
    default boolean addPermission(String permission) {
        return getPermissions().add(permission);
    }

    /**
     * Removes a permission from the command executor.
     * 
     * @param permission The permission.
     * 
     * @return If the permission was removed.
     */
    default boolean removePermission(String permission) {
        return getPermissions().remove(permission);
    }

    /**
     * Sends a message to the command executor.
     * 
     * @param message The message.
     */
    void msg(String message);

    /**
     * Gets the server.
     * 
     * @return The server.
     */
    MinecraftServer getServer();
}
