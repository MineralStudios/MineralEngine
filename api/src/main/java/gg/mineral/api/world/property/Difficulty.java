package gg.mineral.api.world.property;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public enum Difficulty {
    PEACEFUL((byte) 0), EASY((byte) 1), NORMAL((byte) 2), HARD((byte) 3);

    @Getter
    private final byte id;

    public static Difficulty fromId(byte id) {
        for (val difficulty : Difficulty.values())
            if (difficulty.getId() == id)
                return difficulty;

        return null;
    }
}
