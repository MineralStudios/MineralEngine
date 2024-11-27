package gg.mineral.api.world.property;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Dimension {
    NETHER((byte) -1), OVERWORLD((byte) 0), END((byte) 1);

    @Getter
    private final byte id;
}
