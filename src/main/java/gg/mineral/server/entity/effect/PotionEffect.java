package gg.mineral.server.entity.effect;

import java.util.UUID;

import gg.mineral.server.entity.attribute.Attribute;
import gg.mineral.server.entity.attribute.AttributeModifier;
import gg.mineral.server.entity.attribute.AttributeOperation;
import gg.mineral.server.entity.living.human.Player;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
// TODO: complete attribute modifiers
public enum PotionEffect {
    SPEED((byte) 1) {
        @Override
        public void applyAttributes(Player player, int amplifier, double duration) {
            player.getAttribute(Attribute.MOVEMENT_SPEED)
                    .addModifier(new AttributeModifier(UUID.fromString("91AEAA56-376B-4498-935B-2F7F68070635"),
                            "potion.moveSpeed", 0.2f * (amplifier + 1),
                            AttributeOperation.MULTIPLY_TOTAL));
        }
    },
    SLOWNESS((byte) 2) {
        @Override
        public void applyAttributes(Player player, int amplifier, double duration) {
            player.getAttribute(Attribute.MOVEMENT_SPEED)
                    .addModifier(new AttributeModifier(UUID.fromString("7107DE5E-7CE8-4030-940E-514C1F160890"),
                            "potion.moveSlowdown", -0.15f * (amplifier + 1),
                            AttributeOperation.MULTIPLY_TOTAL));
        }
    },
    HASTE((byte) 3) {
        @Override
        public void applyAttributes(Player player, int amplifier, double duration) {
        }
    },
    MINING_FATIGUE((byte) 4) {
        @Override
        public void applyAttributes(Player player, int amplifier, double duration) {
        }
    },
    STRENGTH((byte) 5) {
        @Override
        public void applyAttributes(Player player, int amplifier, double duration) {
        }
    },
    INSTANT_HEALTH((byte) 6) {
        @Override
        public void applyAttributes(Player player, int amplifier, double duration) {
        }
    },
    INSTANT_DAMAGE((byte) 7) {
        @Override
        public void applyAttributes(Player player, int amplifier, double duration) {
        }
    },
    JUMP_BOOST((byte) 8) {
        @Override
        public void applyAttributes(Player player, int amplifier, double duration) {
        }
    },
    NAUSEA((byte) 9) {
        @Override
        public void applyAttributes(Player player, int amplifier, double duration) {
        }
    },
    REGENERATION((byte) 10) {
        @Override
        public void applyAttributes(Player player, int amplifier, double duration) {
        }
    },
    RESISTANCE((byte) 11) {
        @Override
        public void applyAttributes(Player player, int amplifier, double duration) {
        }
    },
    FIRE_RESISTANCE((byte) 12) {
        @Override
        public void applyAttributes(Player player, int amplifier, double duration) {
        }
    },
    WATER_BREATHING((byte) 13) {
        @Override
        public void applyAttributes(Player player, int amplifier, double duration) {
        }
    },
    INVISIBILITY((byte) 14) {
        @Override
        public void applyAttributes(Player player, int amplifier, double duration) {
        }
    },
    BLINDNESS((byte) 15) {
        @Override
        public void applyAttributes(Player player, int amplifier, double duration) {
        }
    },
    NIGHT_VISION((byte) 16) {
        @Override
        public void applyAttributes(Player player, int amplifier, double duration) {
        }
    },
    HUNGER((byte) 17) {
        @Override
        public void applyAttributes(Player player, int amplifier, double duration) {
        }
    },
    WEAKNESS((byte) 18) {
        @Override
        public void applyAttributes(Player player, int amplifier, double duration) {
        }
    },
    POISON((byte) 19) {
        @Override
        public void applyAttributes(Player player, int amplifier, double duration) {
        }
    },
    WITHER((byte) 20) {
        @Override
        public void applyAttributes(Player player, int amplifier, double duration) {
        }
    },
    HEALTH_BOOST((byte) 21) {
        @Override
        public void applyAttributes(Player player, int amplifier, double duration) {
        }
    },
    ABSORPTION((byte) 22) {
        @Override
        public void applyAttributes(Player player, int amplifier, double duration) {
        }
    },
    SATURATION((byte) 23) {
        @Override
        public void applyAttributes(Player player, int amplifier, double duration) {
        }
    },
    GLOWING((byte) 24) {
        @Override
        public void applyAttributes(Player player, int amplifier, double duration) {
        }
    },
    LEVITATION((byte) 25) {
        @Override
        public void applyAttributes(Player player, int amplifier, double duration) {
        }
    },
    LUCK((byte) 26) {
        @Override
        public void applyAttributes(Player player, int amplifier, double duration) {
        }
    },
    UNLUCK((byte) 27) {
        @Override
        public void applyAttributes(Player player, int amplifier, double duration) {
        }
    },
    SLOW_FALLING((byte) 28) {
        @Override
        public void applyAttributes(Player player, int amplifier, double duration) {
        }
    },
    CONDUIT_POWER((byte) 9) {
        @Override
        public void applyAttributes(Player player, int amplifier, double duration) {
        }
    },
    DOLPHINS_GRACE((byte) 30) {
        @Override
        public void applyAttributes(Player player, int amplifier, double duration) {
        }
    },
    BAD_OMEN((byte) 31) {
        @Override
        public void applyAttributes(Player player, int amplifier, double duration) {
        }
    },
    HERO_OF_THE_VILLAGE((byte) 32) {

        @Override
        public void applyAttributes(Player player, int amplifier, double duration) {
        }

    };

    private final byte id;

    public abstract void applyAttributes(Player player, int amplifier, double duration);
}
