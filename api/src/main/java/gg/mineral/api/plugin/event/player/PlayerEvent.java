package gg.mineral.api.plugin.event.player;

import gg.mineral.api.entity.living.human.Player;
import gg.mineral.api.plugin.event.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class PlayerEvent implements Event {
    private final Player player;
}
