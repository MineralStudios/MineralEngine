package gg.mineral.api.entity.living.human.property

enum class Gamemode(val id: Byte) {
    SURVIVAL(0.toByte()), CREATIVE(1.toByte()), ADVENTURE(2.toByte()), HARDCORE(0x8.toByte());
}
