package gg.mineral.api.entity.meta

import gg.mineral.api.entity.Ageable
import gg.mineral.api.entity.Entity
import gg.mineral.api.entity.Living
import gg.mineral.api.entity.living.*
import gg.mineral.api.entity.`object`.*

/**
 * Index constants for entity metadata.
 */
enum class EntityMetadataIndex(val index: Int, val type: EntityMetadataType, val appliesTo: Class<out Entity?>) {
    STATUS(0, EntityMetadataType.BYTE, Entity::class.java),
    AIR_TIME(1, EntityMetadataType.SHORT, Entity::class.java),
    SILENT(4, EntityMetadataType.BYTE, Entity::class.java),

    NAME_TAG(2, EntityMetadataType.STRING, Entity::class.java),
    SHOW_NAME_TAG(3, EntityMetadataType.BYTE, Entity::class.java),

    HEALTH(6, EntityMetadataType.FLOAT, Living::class.java),
    POTION_COLOR(7, EntityMetadataType.INT, Living::class.java),
    POTION_AMBIENT(8, EntityMetadataType.BYTE, Living::class.java),
    ARROW_COUNT(9, EntityMetadataType.BYTE, Living::class.java),
    NO_AI(15, EntityMetadataType.BYTE, Living::class.java),

    AGE(12, EntityMetadataType.BYTE, Ageable::class.java),

    ARMORSTAND_FLAGS(10, EntityMetadataType.BYTE, ArmorStand::class.java),
    ARMORSTAND_HEAD_POSITION(11, EntityMetadataType.EULER_ANGLE, ArmorStand::class.java),
    ARMORSTAND_BODY_POSITION(12, EntityMetadataType.EULER_ANGLE, ArmorStand::class.java),
    ARMORSTAND_LEFT_ARM_POSITION(13, EntityMetadataType.EULER_ANGLE, ArmorStand::class.java),
    ARMORSTAND_RIGHT_ARM_POSITION(14, EntityMetadataType.EULER_ANGLE, ArmorStand::class.java),
    ARMORSTAND_LEFT_LEG_POSITION(15, EntityMetadataType.EULER_ANGLE, ArmorStand::class.java),
    ARMORSTAND_RIGHT_LEG_POSITION(16, EntityMetadataType.EULER_ANGLE, ArmorStand::class.java),

    // allowed to override NAME_TAG from LivingEntity
    PLAYER_SKIN_FLAGS(10, EntityMetadataType.BYTE, Human::class.java),
    PLAYER_FLAGS(16, EntityMetadataType.BYTE, Human::class.java),
    PLAYER_ABSORPTION_HEARTS(17, EntityMetadataType.FLOAT, Human::class.java),
    PLAYER_SCORE(18, EntityMetadataType.INT, Human::class.java),

    HORSE_FLAGS(16, EntityMetadataType.INT, Horse::class.java),
    HORSE_TYPE(19, EntityMetadataType.BYTE, Horse::class.java),
    HORSE_STYLE(20, EntityMetadataType.INT, Horse::class.java),
    HORSE_OWNER(21, EntityMetadataType.STRING, Horse::class.java),
    HORSE_ARMOR(22, EntityMetadataType.INT, Horse::class.java),

    BAT_HANGING(16, EntityMetadataType.BYTE, Bat::class.java),

    OCELOT_FLAGS(16, EntityMetadataType.BYTE, Ocelot::class.java),
    OCELOT_OWNER(17, EntityMetadataType.STRING, Ocelot::class.java),
    OCELOT_TYPE(18, EntityMetadataType.BYTE, Ocelot::class.java),

    WOLF_FLAGS(16, EntityMetadataType.BYTE, Wolf::class.java),
    WOLF_OWNER(17, EntityMetadataType.STRING, Wolf::class.java),
    WOLF_HEALTH(18, EntityMetadataType.FLOAT, Wolf::class.java),
    WOLF_BEGGING(19, EntityMetadataType.BYTE, Wolf::class.java),
    WOLF_COLOR(20, EntityMetadataType.BYTE, Wolf::class.java),

    PIG_SADDLE(16, EntityMetadataType.BYTE, Pig::class.java),

    RABBIT_TYPE(18, EntityMetadataType.BYTE, Rabbit::class.java),

    SHEEP_DATA(16, EntityMetadataType.BYTE, Sheep::class.java),

    VILLAGER_TYPE(16, EntityMetadataType.INT, Villager::class.java),

    ENDERMAN_BLOCK(16, EntityMetadataType.SHORT, Enderman::class.java),
    ENDERMAN_BLOCK_DATA(17, EntityMetadataType.BYTE, Enderman::class.java),
    ENDERMAN_ALERTED(18, EntityMetadataType.BYTE, Enderman::class.java),

    ZOMBIE_IS_CHILD(12, EntityMetadataType.BYTE, Zombie::class.java),
    ZOMBIE_IS_VILLAGER(13, EntityMetadataType.BYTE, Zombie::class.java),
    ZOMBIE_IS_CONVERTING(14, EntityMetadataType.BYTE, Zombie::class.java),

    BLAZE_ON_FIRE(16, EntityMetadataType.BYTE, Blaze::class.java),

    SPIDER_CLIMBING(16, EntityMetadataType.BYTE, Spider::class.java),

    CREEPER_STATE(16, EntityMetadataType.BYTE, Creeper::class.java),
    CREEPER_POWERED(17, EntityMetadataType.BYTE, Creeper::class.java),

    GHAST_ATTACKING(16, EntityMetadataType.BYTE, Ghast::class.java),

    SLIME_SIZE(16, EntityMetadataType.BYTE, Slime::class.java),

    SKELETON_TYPE(13, EntityMetadataType.BYTE, Skeleton::class.java),

    WITCH_AGGRESSIVE(21, EntityMetadataType.BYTE, Witch::class.java),

    GOLEM_PLAYER_BUILT(16, EntityMetadataType.BYTE, IronGolem::class.java),

    WITHER_TARGET_1(17, EntityMetadataType.INT, Wither::class.java),
    WITHER_TARGET_2(18, EntityMetadataType.INT, Wither::class.java),
    WITHER_TARGET_3(19, EntityMetadataType.INT, Wither::class.java),
    WITHER_INVULN_TIME(20, EntityMetadataType.INT, Wither::class.java),

    GUARDIAN_FLAGS(16, EntityMetadataType.BYTE, Guardian::class.java),
    GUARDIAN_TARGET(17, EntityMetadataType.INT, Guardian::class.java),

    BOAT_HIT_TIME(17, EntityMetadataType.INT, Boat::class.java),
    BOAT_DIRECTION(18, EntityMetadataType.INT, Boat::class.java),
    BOAT_DAMAGE_TAKEN(19, EntityMetadataType.FLOAT, Boat::class.java),

    MINECART_SHAKE_POWER(17, EntityMetadataType.INT, Minecart::class.java),
    MINECART_SHAKE_DIRECTION(18, EntityMetadataType.INT, Minecart::class.java),
    MINECART_DAMAGE_TAKEN(19, EntityMetadataType.FLOAT, Minecart::class.java),
    MINECART_BLOCK(20, EntityMetadataType.INT, Minecart::class.java),
    MINECART_BLOCK_OFFSET(21, EntityMetadataType.INT, Minecart::class.java),
    MINECART_BLOCK_SHOWN(22, EntityMetadataType.BYTE, Minecart::class.java),

    FURNACE_MINECART_POWERED(16, EntityMetadataType.BYTE, PoweredMinecart::class.java),

    ITEM_ITEM(10, EntityMetadataType.ITEM, Item::class.java),

    ARROW_CRITICAL(16, EntityMetadataType.BYTE, Arrow::class.java),

    FIREWORK_INFO(8, EntityMetadataType.ITEM, Firework::class.java),

    ITEM_FRAME_ITEM(8, EntityMetadataType.ITEM, ItemFrame::class.java),
    ITEM_FRAME_ROTATION(9, EntityMetadataType.BYTE, ItemFrame::class.java),

    ENDER_CRYSTAL_HEALTH(8, EntityMetadataType.INT, EnderCrystal::class.java);

    fun appliesTo(clazz: Class<out Entity?>): Boolean = appliesTo.isAssignableFrom(clazz)

    interface StatusFlags {
        companion object {
            const val ON_FIRE: Int = 0x01
            const val SNEAKING: Int = 0x02
            const val SPRINTING: Int = 0x08
            const val ARM_UP: Int = 0x10 /* eating, drinking, blocking */
            const val INVISIBLE: Int = 0x20
        }
    }

    interface ArmorStandFlags {
        companion object {
            const val IS_SMALL: Int = 0x01
            const val HAS_GRAVITY: Int = 0x02
            const val HAS_ARMS: Int = 0x04
            const val NO_BASE_PLATE: Int = 0x08
            const val IS_MARKER: Int = 0x10
        }
    }

    interface HorseFlags {
        companion object {
            const val IS_TAME: Int = 0x02
            const val HAS_SADDLE: Int = 0x04
            const val HAS_CHEST: Int = 0x08
            const val IS_BRED: Int = 0x10
            const val IS_EATING: Int = 0x20
            const val IS_REARING: Int = 0x40
            const val MOUTH_OPEN: Int = 0x80
        }
    }

    interface TameableFlags {
        companion object {
            const val IS_SITTING: Int = 0x01
            const val WOLF_IS_ANGRY: Int = 0x02
            const val IS_TAME: Int = 0x04
        }
    }

    companion object {
        @JvmStatic
        fun getIndex(index: Int, type: EntityMetadataType): EntityMetadataIndex? {
            var output: EntityMetadataIndex? = null
            for (entry in entries) if (entry.index == index && entry.type == type) {
                output = entry
                break
            }

            return output
        }
    }
}