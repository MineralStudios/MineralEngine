package gg.mineral.server.command;

import java.util.Set;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

public class CommandExecutor {
    Set<String> permissions = new ObjectOpenHashSet<String>();

    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }

    public void addPermission(String permission) {
        permissions.add(permission);
    }

    public void removePermission(String permission) {
        permissions.remove(permission);
    }
}
