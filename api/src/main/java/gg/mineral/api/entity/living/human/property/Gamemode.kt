package gg.mineral.api.entity.living.human.property

import lombok.AllArgsConstructor
import lombok.Getter

@AllArgsConstructor
@Getter
enum class Gamemode {
    SURVIVAL(0.toByte()), CREATIVE(1.toByte()), ADVENTURE(2.toByte()), HARDCORE(0x8.toByte());

    private val id: Byte = 0
}
