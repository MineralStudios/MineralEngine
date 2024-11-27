package gg.mineral.server.plugin;

import java.util.ArrayList;
import java.util.List;

import gg.mineral.server.plugin.listener.Listener;
import lombok.val;

public class PluginRegistry {
    public static final List<Listener> LISTENERS = new ArrayList<>();

    public static void registerPlugins(MineralPlugin... plugins) {
        for (val plugin : plugins)
            plugin.onEnable();
    }

    public static void unregisterPlugins(MineralPlugin... plugins) {
        for (val plugin : plugins)
            plugin.onDisable();
    }
}
