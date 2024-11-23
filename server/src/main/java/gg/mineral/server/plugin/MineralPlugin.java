package gg.mineral.server.plugin;

import gg.mineral.server.command.Command;
import gg.mineral.server.plugin.listener.Listener;

public interface MineralPlugin {

    void onEnable();

    void onDisable();

    Listener[] getListeners();

    Command[] getCommands();
}
