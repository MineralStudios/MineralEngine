package gg.mineral.server.command;

import java.util.Set;

import gg.mineral.server.MinecraftServer;

public interface CommandExecutor {

    Set<String> getPermissions();

    default boolean hasPermission(String permission) {
        return getPermissions().contains(permission);
    }

    default void addPermission(String permission) {
        getPermissions().add(permission);
    }

    default void removePermission(String permission) {
        getPermissions().remove(permission);
    }

    public void msg(String message);

    MinecraftServer getServer();
}
