package gg.mineral.api.plugin.listener

import gg.mineral.api.plugin.event.Event

interface Listener {
    fun onEvent(event: Event?): Boolean = false
}
