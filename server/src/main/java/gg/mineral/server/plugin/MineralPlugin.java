package gg.mineral.server.plugin;

import gg.mineral.api.MinecraftServer;

public interface MineralPlugin {

    void onEnable();

    void onDisable();

    MinecraftServer getServer();
}
