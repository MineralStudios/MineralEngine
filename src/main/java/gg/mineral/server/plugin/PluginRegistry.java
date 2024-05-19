package gg.mineral.server.plugin;

import gg.mineral.server.plugin.listener.Listener;
import gg.mineral.server.util.collection.GlueList;

import java.util.List;

public class PluginRegistry {
    public static final List<Listener> LISTENERS = new GlueList<>();

    public static void registerPlugins(MineralPlugin... plugins) {
        for (MineralPlugin plugin : plugins)
            plugin.onEnable();
    }

    public static void unregisterPlugins(MineralPlugin... plugins) {
        for (MineralPlugin plugin : plugins)
            plugin.onDisable();
    }
}
