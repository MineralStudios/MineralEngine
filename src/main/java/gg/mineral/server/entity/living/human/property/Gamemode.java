package gg.mineral.server.entity.living.human.property;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Gamemode {
    SURVIVAL((short) 0), CREATIVE((short) 1), ADVENTURE((short) 2), HARDCORE((short) 0x8);

    @Getter
    short id;
}
