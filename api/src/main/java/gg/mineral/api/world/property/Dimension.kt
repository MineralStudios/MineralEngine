package gg.mineral.api.world.property

import lombok.Getter
import lombok.RequiredArgsConstructor

@RequiredArgsConstructor
enum class Dimension {
    NETHER(-1.toByte()), OVERWORLD(0.toByte()), END(1.toByte());

    @Getter
    private val id: Byte = 0
}
