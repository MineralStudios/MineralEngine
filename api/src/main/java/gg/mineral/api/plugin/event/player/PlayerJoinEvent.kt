package gg.mineral.api.plugin.event.player

import gg.mineral.api.entity.living.human.Player
import lombok.Getter
import lombok.Setter

@Getter
@Setter
class PlayerJoinEvent(player: Player) : PlayerEvent(player) {
    private val x = player.x
    private val y = player.y
    private val z = player.z
}
