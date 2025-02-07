package gg.mineral.api.plugin.listener

import gg.mineral.api.plugin.event.Event
import gg.mineral.api.plugin.event.player.PlayerEvent
import gg.mineral.api.plugin.event.player.PlayerJoinEvent

interface Listener {
    fun onEvent(event: Event): Boolean {
        return when (event) {
            is PlayerJoinEvent -> onPlayerJoin(event)
            is PlayerEvent -> onPlayer(event)
            else -> false
        }
    }

    fun onPlayer(event: Event): Boolean = false

    fun onPlayerJoin(event: Event): Boolean = false
}
