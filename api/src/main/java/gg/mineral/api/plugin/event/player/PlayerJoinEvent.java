package gg.mineral.api.plugin.event.player;

import gg.mineral.api.entity.living.human.Player;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerJoinEvent extends PlayerEvent {
    private double x, y, z;

    public PlayerJoinEvent(Player player) {
        super(player);
        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();
    }
}
