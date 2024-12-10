package gg.mineral.api.plugin.event.player

import gg.mineral.api.entity.living.human.Player
import gg.mineral.api.plugin.event.Event
import lombok.Getter
import lombok.RequiredArgsConstructor

@RequiredArgsConstructor
@Getter
abstract class PlayerEvent : Event {
    private val player: Player? = null
}
