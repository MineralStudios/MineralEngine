package gg.mineral.api.world.property

import lombok.Getter
import lombok.RequiredArgsConstructor

@RequiredArgsConstructor
enum class Difficulty {
    PEACEFUL(0.toByte()), EASY(1.toByte()), NORMAL(2.toByte()), HARD(3.toByte());

    @Getter
    private val id: Byte = 0

    companion object {
        fun fromId(id: Byte): Difficulty? {
            for (difficulty in entries) if (difficulty.getId() == id) return difficulty

            return null
        }
    }
}
