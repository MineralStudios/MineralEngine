package gg.mineral.api.world.property

enum class LevelType(val type: String) {
    DEFAULT("default"), FLAT("flat"), LARGE_BIOMES("largeBiomes"), AMPLIFIED("amplified"), DEFAULT_1_1("default_1_1");

    fun string(): String = type
}
