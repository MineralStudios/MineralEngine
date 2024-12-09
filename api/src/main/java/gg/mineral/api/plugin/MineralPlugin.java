package gg.mineral.api.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gg.mineral.api.command.Command;
import gg.mineral.api.plugin.listener.Listener;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;

@Getter
public abstract class MineralPlugin {
    private final List<Listener> listeners = new ArrayList<>();
    private final Map<String, Command> commands = new Object2ObjectOpenHashMap<>();

    public void registerListener(Listener... listeners) {
        for (Listener listener : listeners)
            this.listeners.add(listener);
    }

    public void registerCommand(Command... commands) {
        for (Command command : commands)
            this.commands.put(command.getName(), command);
    }

    public boolean hasCommand(String name) {
        return commands.containsKey(name);
    }

    public abstract void onEnable();

    public abstract void onDisable();
}
