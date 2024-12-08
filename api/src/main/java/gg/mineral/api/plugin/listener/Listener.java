package gg.mineral.api.plugin.listener;

import gg.mineral.api.plugin.event.Event;

public interface Listener {
    default boolean onEvent(Event event) {
        return false;
    }
}
