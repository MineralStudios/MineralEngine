package gg.mineral.api.world.property

import lombok.RequiredArgsConstructor

@RequiredArgsConstructor
enum class LevelType {
    DEFAULT("default"), FLAT("flat"), LARGE_BIOMES("largeBiomes"), AMPLIFIED("amplified"), DEFAULT_1_1("default_1_1");

    private val type: String? = null

    fun string(): String? {
        return type
    }
}
