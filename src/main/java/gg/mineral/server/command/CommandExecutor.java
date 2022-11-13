package gg.mineral.server.command;

import java.util.Set;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

public interface CommandExecutor {
    Set<String> permissions = new ObjectOpenHashSet<String>();

    default boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }

    default void addPermission(String permission) {
        permissions.add(permission);
    }

    default void removePermission(String permission) {
        permissions.remove(permission);
    }
}
