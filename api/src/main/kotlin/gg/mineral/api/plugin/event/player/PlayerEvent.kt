package gg.mineral.api.plugin.event.player

import gg.mineral.api.entity.living.human.Player
import gg.mineral.api.plugin.event.Event

abstract class PlayerEvent(val player: Player) : Event
