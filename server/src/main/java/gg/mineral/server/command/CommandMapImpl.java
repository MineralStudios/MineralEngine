package gg.mineral.server.command;

import java.util.Collection;

import gg.mineral.api.command.Command;
import gg.mineral.api.command.CommandMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.val;

public class CommandMapImpl extends Object2ObjectOpenHashMap<String, Command> implements CommandMap {
    @Override
    public boolean register(Command command) {
        if (containsKey(command.getName()))
            return false;

        put(command.getName(), command);
        return true;
    }

    @Override
    public boolean unregister(Command command) {
        return remove(command.getName()) != null;
    }

    @Override
    public boolean registerAll(Collection<? extends Command> commands) {
        boolean success = true;
        for (val command : commands)
            success &= register(command);
        return success;
    }
}
