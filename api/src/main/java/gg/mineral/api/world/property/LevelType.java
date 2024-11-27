package gg.mineral.api.world.property;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum LevelType {
    DEFAULT("default"), FLAT("flat"), LARGE_BIOMES("largeBiomes"), AMPLIFIED("amplified"), DEFAULT_1_1("default_1_1");

    private final String type;

    public String string() {
        return type;
    }
}
