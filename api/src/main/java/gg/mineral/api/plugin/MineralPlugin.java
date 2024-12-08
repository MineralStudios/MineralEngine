package gg.mineral.api.plugin;

import gg.mineral.api.MinecraftServer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class MineralPlugin {
    private final MinecraftServer server;

    public abstract void onEnable();

    public abstract void onDisable();
}
