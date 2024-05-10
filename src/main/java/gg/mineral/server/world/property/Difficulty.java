package gg.mineral.server.world.property;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Difficulty {
    PEACEFUL((byte) 0), EASY((byte) 1), NORMAL((byte) 2), HARD((byte) 3);

    @Getter
    byte id;

    public static Difficulty fromId(byte id) {
        for (Difficulty difficulty : Difficulty.values()) {
            if (difficulty.getId() == id) {
                return difficulty;
            }
        }
        return null;
    }
}
