package gg.mineral.server.plugin.listener;

import gg.mineral.server.entity.living.human.Player;

public interface JoinListener {
    void onJoin(Player player);

    void onQuit(Player player);
}
