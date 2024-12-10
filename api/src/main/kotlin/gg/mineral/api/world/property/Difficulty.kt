package gg.mineral.api.world.property

enum class Difficulty(val id: Byte) {
    PEACEFUL(0.toByte()), EASY(1.toByte()), NORMAL(2.toByte()), HARD(3.toByte());

    companion object {
        fun fromId(id: Byte): Difficulty? {
            for (difficulty in entries) if (difficulty.id == id) return difficulty
            return null
        }
    }
}
