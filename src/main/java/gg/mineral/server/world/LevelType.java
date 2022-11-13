package gg.mineral.server.world;

public enum LevelType {
    DEFAULT("default"), FLAT("flat"), LARGE_BIOMES("largeBiomes"), AMPLIFIED("amplified"), DEFAULT_1_1("default_1_1");

    String type;

    LevelType(String type) {
        this.type = type;
    }

    public String string() {
        return type;
    }
}
