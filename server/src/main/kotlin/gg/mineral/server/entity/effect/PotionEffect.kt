package gg.mineral.server.entity.effect

import com.eatthepath.uuid.FastUUID
import gg.mineral.api.entity.attribute.Attribute
import gg.mineral.api.entity.attribute.AttributeModifier
import gg.mineral.api.entity.attribute.AttributeOperation
import gg.mineral.server.entity.living.human.PlayerImpl

// TODO: complete attribute modifiers, move to api
enum class PotionEffect(val id: Byte) {
    SPEED(1.toByte()) {
        override fun applyAttributes(player: PlayerImpl, amplifier: Int, duration: Double) {
            player.getAttribute(Attribute.MOVEMENT_SPEED)
                .addModifier(
                    AttributeModifier(
                        FastUUID.parseUUID("91AEAA56-376B-4498-935B-2F7F68070635"),
                        "potion.moveSpeed", 0.2f * (amplifier + 1),
                        AttributeOperation.MULTIPLY_TOTAL
                    )
                )
        }
    },
    SLOWNESS(2.toByte()) {
        override fun applyAttributes(player: PlayerImpl, amplifier: Int, duration: Double) {
            player.getAttribute(Attribute.MOVEMENT_SPEED)
                .addModifier(
                    AttributeModifier(
                        FastUUID.parseUUID("7107DE5E-7CE8-4030-940E-514C1F160890"),
                        "potion.moveSlowdown", -0.15f * (amplifier + 1),
                        AttributeOperation.MULTIPLY_TOTAL
                    )
                )
        }
    },
    HASTE(3.toByte()) {
        override fun applyAttributes(player: PlayerImpl, amplifier: Int, duration: Double) {
        }
    },
    MINING_FATIGUE(4.toByte()) {
        override fun applyAttributes(player: PlayerImpl, amplifier: Int, duration: Double) {
        }
    },
    STRENGTH(5.toByte()) {
        override fun applyAttributes(player: PlayerImpl, amplifier: Int, duration: Double) {
        }
    },
    INSTANT_HEALTH(6.toByte()) {
        override fun applyAttributes(player: PlayerImpl, amplifier: Int, duration: Double) {
        }
    },
    INSTANT_DAMAGE(7.toByte()) {
        override fun applyAttributes(player: PlayerImpl, amplifier: Int, duration: Double) {
        }
    },
    JUMP_BOOST(8.toByte()) {
        override fun applyAttributes(player: PlayerImpl, amplifier: Int, duration: Double) {
        }
    },
    NAUSEA(9.toByte()) {
        override fun applyAttributes(player: PlayerImpl, amplifier: Int, duration: Double) {
        }
    },
    REGENERATION(10.toByte()) {
        override fun applyAttributes(player: PlayerImpl, amplifier: Int, duration: Double) {
        }
    },
    RESISTANCE(11.toByte()) {
        override fun applyAttributes(player: PlayerImpl, amplifier: Int, duration: Double) {
        }
    },
    FIRE_RESISTANCE(12.toByte()) {
        override fun applyAttributes(player: PlayerImpl, amplifier: Int, duration: Double) {
        }
    },
    WATER_BREATHING(13.toByte()) {
        override fun applyAttributes(player: PlayerImpl, amplifier: Int, duration: Double) {
        }
    },
    INVISIBILITY(14.toByte()) {
        override fun applyAttributes(player: PlayerImpl, amplifier: Int, duration: Double) {
        }
    },
    BLINDNESS(15.toByte()) {
        override fun applyAttributes(player: PlayerImpl, amplifier: Int, duration: Double) {
        }
    },
    NIGHT_VISION(16.toByte()) {
        override fun applyAttributes(player: PlayerImpl, amplifier: Int, duration: Double) {
        }
    },
    HUNGER(17.toByte()) {
        override fun applyAttributes(player: PlayerImpl, amplifier: Int, duration: Double) {
        }
    },
    WEAKNESS(18.toByte()) {
        override fun applyAttributes(player: PlayerImpl, amplifier: Int, duration: Double) {
        }
    },
    POISON(19.toByte()) {
        override fun applyAttributes(player: PlayerImpl, amplifier: Int, duration: Double) {
        }
    },
    WITHER(20.toByte()) {
        override fun applyAttributes(player: PlayerImpl, amplifier: Int, duration: Double) {
        }
    },
    HEALTH_BOOST(21.toByte()) {
        override fun applyAttributes(player: PlayerImpl, amplifier: Int, duration: Double) {
        }
    },
    ABSORPTION(22.toByte()) {
        override fun applyAttributes(player: PlayerImpl, amplifier: Int, duration: Double) {
        }
    },
    SATURATION(23.toByte()) {
        override fun applyAttributes(player: PlayerImpl, amplifier: Int, duration: Double) {
        }
    },
    GLOWING(24.toByte()) {
        override fun applyAttributes(player: PlayerImpl, amplifier: Int, duration: Double) {
        }
    },
    LEVITATION(25.toByte()) {
        override fun applyAttributes(player: PlayerImpl, amplifier: Int, duration: Double) {
        }
    },
    LUCK(26.toByte()) {
        override fun applyAttributes(player: PlayerImpl, amplifier: Int, duration: Double) {
        }
    },
    UNLUCK(27.toByte()) {
        override fun applyAttributes(player: PlayerImpl, amplifier: Int, duration: Double) {
        }
    };

    abstract fun applyAttributes(player: PlayerImpl, amplifier: Int, duration: Double)
}
