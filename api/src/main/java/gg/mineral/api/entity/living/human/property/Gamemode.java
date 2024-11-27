package gg.mineral.api.entity.living.human.property;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Gamemode {
    SURVIVAL((byte) 0), CREATIVE((byte) 1), ADVENTURE((byte) 2), HARDCORE((byte) 0x8);

    private final byte id;
}
