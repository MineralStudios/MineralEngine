package gg.mineral.api.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gg.mineral.api.command.Command;
import gg.mineral.api.plugin.listener.Listener;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;

@Getter
public abstract class MineralPlugin {
    private final List<Listener> listeners = new ArrayList<>();
    private final Map<String, Command> commands = new Object2ObjectOpenHashMap<>();

    @SneakyThrows
    public void registerListener(Class<?>... listenerClasses) {
        for (val listenerClass : listenerClasses) {
            val classLoader = listenerClass.getClassLoader();
            val generated = classLoader.loadClass(listenerClass.getName() + "_Generated").asSubclass(Listener.class);
            val listener = generated.getDeclaredConstructor().newInstance();
            this.listeners.add(listener);
        }
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
