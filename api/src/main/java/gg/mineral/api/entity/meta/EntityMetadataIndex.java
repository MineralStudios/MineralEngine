package gg.mineral.api.entity.meta;

import gg.mineral.api.entity.Ageable;
import gg.mineral.api.entity.Entity;
import gg.mineral.api.entity.Living;
import gg.mineral.api.entity.living.Bat;
import gg.mineral.api.entity.living.Blaze;
import gg.mineral.api.entity.living.Creeper;
import gg.mineral.api.entity.living.Enderman;
import gg.mineral.api.entity.living.Ghast;
import gg.mineral.api.entity.living.Guardian;
import gg.mineral.api.entity.living.Horse;
import gg.mineral.api.entity.living.Human;
import gg.mineral.api.entity.living.IronGolem;
import gg.mineral.api.entity.living.Ocelot;
import gg.mineral.api.entity.living.Pig;
import gg.mineral.api.entity.living.Rabbit;
import gg.mineral.api.entity.living.Sheep;
import gg.mineral.api.entity.living.Skeleton;
import gg.mineral.api.entity.living.Slime;
import gg.mineral.api.entity.living.Spider;
import gg.mineral.api.entity.living.Villager;
import gg.mineral.api.entity.living.Witch;
import gg.mineral.api.entity.living.Wither;
import gg.mineral.api.entity.living.Wolf;
import gg.mineral.api.entity.living.Zombie;
import gg.mineral.api.entity.object.ArmorStand;
import gg.mineral.api.entity.object.Arrow;
import gg.mineral.api.entity.object.Boat;
import gg.mineral.api.entity.object.EnderCrystal;
import gg.mineral.api.entity.object.Firework;
import gg.mineral.api.entity.object.Item;
import gg.mineral.api.entity.object.ItemFrame;
import gg.mineral.api.entity.object.Minecart;
import gg.mineral.api.entity.object.PoweredMinecart;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * Index constants for entity metadata.
 */
@RequiredArgsConstructor
@Getter
public enum EntityMetadataIndex {

    STATUS(0, EntityMetadataType.BYTE, Entity.class),
    AIR_TIME(1, EntityMetadataType.SHORT, Entity.class),
    SILENT(4, EntityMetadataType.BYTE, Entity.class),

    NAME_TAG(2, EntityMetadataType.STRING, Entity.class),
    SHOW_NAME_TAG(3, EntityMetadataType.BYTE, Entity.class),

    HEALTH(6, EntityMetadataType.FLOAT, Living.class),
    POTION_COLOR(7, EntityMetadataType.INT, Living.class),
    POTION_AMBIENT(8, EntityMetadataType.BYTE, Living.class),
    ARROW_COUNT(9, EntityMetadataType.BYTE, Living.class),
    NO_AI(15, EntityMetadataType.BYTE, Living.class),

    AGE(12, EntityMetadataType.BYTE, Ageable.class),

    ARMORSTAND_FLAGS(10, EntityMetadataType.BYTE, ArmorStand.class),
    ARMORSTAND_HEAD_POSITION(11, EntityMetadataType.EULER_ANGLE, ArmorStand.class),
    ARMORSTAND_BODY_POSITION(12, EntityMetadataType.EULER_ANGLE, ArmorStand.class),
    ARMORSTAND_LEFT_ARM_POSITION(13, EntityMetadataType.EULER_ANGLE, ArmorStand.class),
    ARMORSTAND_RIGHT_ARM_POSITION(14, EntityMetadataType.EULER_ANGLE, ArmorStand.class),
    ARMORSTAND_LEFT_LEG_POSITION(15, EntityMetadataType.EULER_ANGLE, ArmorStand.class),
    ARMORSTAND_RIGHT_LEG_POSITION(16, EntityMetadataType.EULER_ANGLE, ArmorStand.class),

    // allowed to override NAME_TAG from LivingEntity
    PLAYER_SKIN_FLAGS(10, EntityMetadataType.BYTE, Human.class),
    PLAYER_FLAGS(16, EntityMetadataType.BYTE, Human.class),
    PLAYER_ABSORPTION_HEARTS(17, EntityMetadataType.FLOAT, Human.class),
    PLAYER_SCORE(18, EntityMetadataType.INT, Human.class),

    HORSE_FLAGS(16, EntityMetadataType.INT, Horse.class),
    HORSE_TYPE(19, EntityMetadataType.BYTE, Horse.class),
    HORSE_STYLE(20, EntityMetadataType.INT, Horse.class),
    HORSE_OWNER(21, EntityMetadataType.STRING, Horse.class),
    HORSE_ARMOR(22, EntityMetadataType.INT, Horse.class),

    BAT_HANGING(16, EntityMetadataType.BYTE, Bat.class),

    OCELOT_FLAGS(16, EntityMetadataType.BYTE, Ocelot.class),
    OCELOT_OWNER(17, EntityMetadataType.STRING, Ocelot.class),
    OCELOT_TYPE(18, EntityMetadataType.BYTE, Ocelot.class),

    WOLF_FLAGS(16, EntityMetadataType.BYTE, Wolf.class),
    WOLF_OWNER(17, EntityMetadataType.STRING, Wolf.class),
    WOLF_HEALTH(18, EntityMetadataType.FLOAT, Wolf.class),
    WOLF_BEGGING(19, EntityMetadataType.BYTE, Wolf.class),
    WOLF_COLOR(20, EntityMetadataType.BYTE, Wolf.class),

    PIG_SADDLE(16, EntityMetadataType.BYTE, Pig.class),

    RABBIT_TYPE(18, EntityMetadataType.BYTE, Rabbit.class),

    SHEEP_DATA(16, EntityMetadataType.BYTE, Sheep.class),

    VILLAGER_TYPE(16, EntityMetadataType.INT, Villager.class),

    ENDERMAN_BLOCK(16, EntityMetadataType.SHORT, Enderman.class),
    ENDERMAN_BLOCK_DATA(17, EntityMetadataType.BYTE, Enderman.class),
    ENDERMAN_ALERTED(18, EntityMetadataType.BYTE, Enderman.class),

    ZOMBIE_IS_CHILD(12, EntityMetadataType.BYTE, Zombie.class),
    ZOMBIE_IS_VILLAGER(13, EntityMetadataType.BYTE, Zombie.class),
    ZOMBIE_IS_CONVERTING(14, EntityMetadataType.BYTE, Zombie.class),

    BLAZE_ON_FIRE(16, EntityMetadataType.BYTE, Blaze.class),

    SPIDER_CLIMBING(16, EntityMetadataType.BYTE, Spider.class),

    CREEPER_STATE(16, EntityMetadataType.BYTE, Creeper.class),
    CREEPER_POWERED(17, EntityMetadataType.BYTE, Creeper.class),

    GHAST_ATTACKING(16, EntityMetadataType.BYTE, Ghast.class),

    SLIME_SIZE(16, EntityMetadataType.BYTE, Slime.class),

    SKELETON_TYPE(13, EntityMetadataType.BYTE, Skeleton.class),

    WITCH_AGGRESSIVE(21, EntityMetadataType.BYTE, Witch.class),

    GOLEM_PLAYER_BUILT(16, EntityMetadataType.BYTE, IronGolem.class),

    WITHER_TARGET_1(17, EntityMetadataType.INT, Wither.class),
    WITHER_TARGET_2(18, EntityMetadataType.INT, Wither.class),
    WITHER_TARGET_3(19, EntityMetadataType.INT, Wither.class),
    WITHER_INVULN_TIME(20, EntityMetadataType.INT, Wither.class),

    GUARDIAN_FLAGS(16, EntityMetadataType.BYTE, Guardian.class),
    GUARDIAN_TARGET(17, EntityMetadataType.INT, Guardian.class),

    BOAT_HIT_TIME(17, EntityMetadataType.INT, Boat.class),
    BOAT_DIRECTION(18, EntityMetadataType.INT, Boat.class),
    BOAT_DAMAGE_TAKEN(19, EntityMetadataType.FLOAT, Boat.class),

    MINECART_SHAKE_POWER(17, EntityMetadataType.INT, Minecart.class),
    MINECART_SHAKE_DIRECTION(18, EntityMetadataType.INT, Minecart.class),
    MINECART_DAMAGE_TAKEN(19, EntityMetadataType.FLOAT, Minecart.class),
    MINECART_BLOCK(20, EntityMetadataType.INT, Minecart.class),
    MINECART_BLOCK_OFFSET(21, EntityMetadataType.INT, Minecart.class),
    MINECART_BLOCK_SHOWN(22, EntityMetadataType.BYTE, Minecart.class),

    FURNACE_MINECART_POWERED(16, EntityMetadataType.BYTE, PoweredMinecart.class),

    ITEM_ITEM(10, EntityMetadataType.ITEM, Item.class),

    ARROW_CRITICAL(16, EntityMetadataType.BYTE, Arrow.class),

    FIREWORK_INFO(8, EntityMetadataType.ITEM, Firework.class),

    ITEM_FRAME_ITEM(8, EntityMetadataType.ITEM, ItemFrame.class),
    ITEM_FRAME_ROTATION(9, EntityMetadataType.BYTE, ItemFrame.class),

    ENDER_CRYSTAL_HEALTH(8, EntityMetadataType.INT, EnderCrystal.class);

    private final int index;
    private final EntityMetadataType type;
    private final Class<? extends Entity> appliesTo;

    public static EntityMetadataIndex getIndex(int index, EntityMetadataType type) {
        EntityMetadataIndex output = null;
        for (val entry : values())
            if (entry.getIndex() == index && entry.getType().equals(type)) {
                output = entry;
                break;
            }

        return output;
    }

    public boolean appliesTo(Class<? extends Entity> clazz) {
        return appliesTo.isAssignableFrom(clazz);
    }

    public interface StatusFlags {
        int ON_FIRE = 0x01, SNEAKING = 0x02, SPRINTING = 0x08, ARM_UP = 0x10 /* eating, drinking, blocking */,
                INVISIBLE = 0x20;
    }

    public interface ArmorStandFlags {
        int IS_SMALL = 0x01, HAS_GRAVITY = 0x02, HAS_ARMS = 0x04, NO_BASE_PLATE = 0x08, IS_MARKER = 0x10;
    }

    public interface HorseFlags {
        int IS_TAME = 0x02, HAS_SADDLE = 0x04, HAS_CHEST = 0x08, IS_BRED = 0x10, IS_EATING = 0x20, IS_REARING = 0x40,
                MOUTH_OPEN = 0x80;
    }

    public interface TameableFlags {
        int IS_SITTING = 0x01, WOLF_IS_ANGRY = 0x02, IS_TAME = 0x04;
    }
}