package gg.mineral.server.plugin.listener;

import gg.mineral.server.plugin.event.Event;

public interface Listener {
    default boolean onEvent(Event event) {
        return false;
    }
}
