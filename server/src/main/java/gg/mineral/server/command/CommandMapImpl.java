package gg.mineral.server.command;

import gg.mineral.api.command.Command;
import gg.mineral.api.command.CommandMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

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
}
