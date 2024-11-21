package gg.mineral.server.entity.effect;

import java.util.UUID;

import gg.mineral.server.entity.attribute.AttributeModifier;
import gg.mineral.server.entity.attribute.AttributeOperation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
// TODO: complete attribute modifiers
public enum PotionEffect {
    SPEED((byte) 1) {
        @Override
        public AttributeModifier getModifier(int amplifier, double duration) {
            return new AttributeModifier(UUID.fromString("91AEAA56-376B-4498-935B-2F7F68070635"),
                    "potion.moveSpeed", 0.2f * (amplifier + 1),
                    AttributeOperation.MULTIPLY_TOTAL);
        }
    },
    SLOWNESS((byte) 2) {
        @Override
        public AttributeModifier getModifier(int amplifier, double duration) {
            return new AttributeModifier(UUID.fromString("7107DE5E-7CE8-4030-940E-514C1F160890"),
                    "potion.moveSlowdown", -0.15f * (amplifier + 1),
                    AttributeOperation.MULTIPLY_TOTAL);
        }
    },
    HASTE((byte) 3) {
        @Override
        public AttributeModifier getModifier(int amplifier, double duration) {
            return new AttributeModifier(UUID.fromString("91AEAA56-376B-4498-935B-2F7F68070635"),
                    "potion.moveSpeed", 0.2f * (amplifier + 1),
                    AttributeOperation.MULTIPLY_TOTAL);
        }
    },
    MINING_FATIGUE((byte) 4) {
        @Override
        public AttributeModifier getModifier(int amplifier, double duration) {
            return new AttributeModifier(UUID.fromString("91AEAA56-376B-4498-935B-2F7F68070635"),
                    "potion.moveSpeed", 0.2f * (amplifier + 1),
                    AttributeOperation.MULTIPLY_TOTAL);
        }
    },
    STRENGTH((byte) 5) {
        @Override
        public AttributeModifier getModifier(int amplifier, double duration) {
            return new AttributeModifier(UUID.fromString("91AEAA56-376B-4498-935B-2F7F68070635"),
                    "potion.moveSpeed", 0.2f * (amplifier + 1),
                    AttributeOperation.MULTIPLY_TOTAL);
        }
    },
    INSTANT_HEALTH((byte) 6) {
        @Override
        public AttributeModifier getModifier(int amplifier, double duration) {
            return new AttributeModifier(UUID.fromString("91AEAA56-376B-4498-935B-2F7F68070635"),
                    "potion.moveSpeed", 0.2f * (amplifier + 1),
                    AttributeOperation.MULTIPLY_TOTAL);
        }
    },
    INSTANT_DAMAGE((byte) 7) {
        @Override
        public AttributeModifier getModifier(int amplifier, double duration) {
            return new AttributeModifier(UUID.fromString("91AEAA56-376B-4498-935B-2F7F68070635"),
                    "potion.moveSpeed", 0.2f * (amplifier + 1),
                    AttributeOperation.MULTIPLY_TOTAL);
        }
    },
    JUMP_BOOST((byte) 8) {
        @Override
        public AttributeModifier getModifier(int amplifier, double duration) {
            return new AttributeModifier(UUID.fromString("91AEAA56-376B-4498-935B-2F7F68070635"),
                    "potion.moveSpeed", 0.2f * (amplifier + 1),
                    AttributeOperation.MULTIPLY_TOTAL);
        }
    },
    NAUSEA((byte) 9) {
        @Override
        public AttributeModifier getModifier(int amplifier, double duration) {
            return new AttributeModifier(UUID.fromString("91AEAA56-376B-4498-935B-2F7F68070635"),
                    "potion.moveSpeed", 0.2f * (amplifier + 1),
                    AttributeOperation.MULTIPLY_TOTAL);
        }
    },
    REGENERATION((byte) 10) {
        @Override
        public AttributeModifier getModifier(int amplifier, double duration) {
            return new AttributeModifier(UUID.fromString("91AEAA56-376B-4498-935B-2F7F68070635"),
                    "potion.moveSpeed", 0.2f * (amplifier + 1),
                    AttributeOperation.MULTIPLY_TOTAL);
        }
    },
    RESISTANCE((byte) 11) {
        @Override
        public AttributeModifier getModifier(int amplifier, double duration) {
            return new AttributeModifier(UUID.fromString("91AEAA56-376B-4498-935B-2F7F68070635"),
                    "potion.moveSpeed", 0.2f * (amplifier + 1),
                    AttributeOperation.MULTIPLY_TOTAL);
        }
    },
    FIRE_RESISTANCE((byte) 12) {
        @Override
        public AttributeModifier getModifier(int amplifier, double duration) {
            return new AttributeModifier(UUID.fromString("91AEAA56-376B-4498-935B-2F7F68070635"),
                    "potion.moveSpeed", 0.2f * (amplifier + 1),
                    AttributeOperation.MULTIPLY_TOTAL);
        }
    },
    WATER_BREATHING((byte) 13) {
        @Override
        public AttributeModifier getModifier(int amplifier, double duration) {
            return new AttributeModifier(UUID.fromString("91AEAA56-376B-4498-935B-2F7F68070635"),
                    "potion.moveSpeed", 0.2f * (amplifier + 1),
                    AttributeOperation.MULTIPLY_TOTAL);
        }
    },
    INVISIBILITY((byte) 14) {
        @Override
        public AttributeModifier getModifier(int amplifier, double duration) {
            return new AttributeModifier(UUID.fromString("91AEAA56-376B-4498-935B-2F7F68070635"),
                    "potion.moveSpeed", 0.2f * (amplifier + 1),
                    AttributeOperation.MULTIPLY_TOTAL);
        }
    },
    BLINDNESS((byte) 15) {
        @Override
        public AttributeModifier getModifier(int amplifier, double duration) {
            return new AttributeModifier(UUID.fromString("91AEAA56-376B-4498-935B-2F7F68070635"),
                    "potion.moveSpeed", 0.2f * (amplifier + 1),
                    AttributeOperation.MULTIPLY_TOTAL);
        }
    },
    NIGHT_VISION((byte) 16) {
        @Override
        public AttributeModifier getModifier(int amplifier, double duration) {
            return new AttributeModifier(UUID.fromString("91AEAA56-376B-4498-935B-2F7F68070635"),
                    "potion.moveSpeed", 0.2f * (amplifier + 1),
                    AttributeOperation.MULTIPLY_TOTAL);
        }
    },
    HUNGER((byte) 17) {
        @Override
        public AttributeModifier getModifier(int amplifier, double duration) {
            return new AttributeModifier(UUID.fromString("91AEAA56-376B-4498-935B-2F7F68070635"),
                    "potion.moveSpeed", 0.2f * (amplifier + 1),
                    AttributeOperation.MULTIPLY_TOTAL);
        }
    },
    WEAKNESS((byte) 18) {
        @Override
        public AttributeModifier getModifier(int amplifier, double duration) {
            return new AttributeModifier(UUID.fromString("91AEAA56-376B-4498-935B-2F7F68070635"),
                    "potion.moveSpeed", 0.2f * (amplifier + 1),
                    AttributeOperation.MULTIPLY_TOTAL);
        }
    },
    POISON((byte) 19) {
        @Override
        public AttributeModifier getModifier(int amplifier, double duration) {
            return new AttributeModifier(UUID.fromString("91AEAA56-376B-4498-935B-2F7F68070635"),
                    "potion.moveSpeed", 0.2f * (amplifier + 1),
                    AttributeOperation.MULTIPLY_TOTAL);
        }
    },
    WITHER((byte) 20) {
        @Override
        public AttributeModifier getModifier(int amplifier, double duration) {
            return new AttributeModifier(UUID.fromString("91AEAA56-376B-4498-935B-2F7F68070635"),
                    "potion.moveSpeed", 0.2f * (amplifier + 1),
                    AttributeOperation.MULTIPLY_TOTAL);
        }
    },
    HEALTH_BOOST((byte) 21) {
        @Override
        public AttributeModifier getModifier(int amplifier, double duration) {
            return new AttributeModifier(UUID.fromString("91AEAA56-376B-4498-935B-2F7F68070635"),
                    "potion.moveSpeed", 0.2f * (amplifier + 1),
                    AttributeOperation.MULTIPLY_TOTAL);
        }
    },
    ABSORPTION((byte) 22) {
        @Override
        public AttributeModifier getModifier(int amplifier, double duration) {
            return new AttributeModifier(UUID.fromString("91AEAA56-376B-4498-935B-2F7F68070635"),
                    "potion.moveSpeed", 0.2f * (amplifier + 1),
                    AttributeOperation.MULTIPLY_TOTAL);
        }
    },
    SATURATION((byte) 23) {
        @Override
        public AttributeModifier getModifier(int amplifier, double duration) {
            return new AttributeModifier(UUID.fromString("91AEAA56-376B-4498-935B-2F7F68070635"),
                    "potion.moveSpeed", 0.2f * (amplifier + 1),
                    AttributeOperation.MULTIPLY_TOTAL);
        }
    },
    GLOWING((byte) 24) {
        @Override
        public AttributeModifier getModifier(int amplifier, double duration) {
            return new AttributeModifier(UUID.fromString("91AEAA56-376B-4498-935B-2F7F68070635"),
                    "potion.moveSpeed", 0.2f * (amplifier + 1),
                    AttributeOperation.MULTIPLY_TOTAL);
        }
    },
    LEVITATION((byte) 25) {
        @Override
        public AttributeModifier getModifier(int amplifier, double duration) {
            return new AttributeModifier(UUID.fromString("91AEAA56-376B-4498-935B-2F7F68070635"),
                    "potion.moveSpeed", 0.2f * (amplifier + 1),
                    AttributeOperation.MULTIPLY_TOTAL);
        }
    },
    LUCK((byte) 26) {
        @Override
        public AttributeModifier getModifier(int amplifier, double duration) {
            return new AttributeModifier(UUID.fromString("91AEAA56-376B-4498-935B-2F7F68070635"),
                    "potion.moveSpeed", 0.2f * (amplifier + 1),
                    AttributeOperation.MULTIPLY_TOTAL);
        }
    },
    UNLUCK((byte) 27) {
        @Override
        public AttributeModifier getModifier(int amplifier, double duration) {
            return new AttributeModifier(UUID.fromString("91AEAA56-376B-4498-935B-2F7F68070635"),
                    "potion.moveSpeed", 0.2f * (amplifier + 1),
                    AttributeOperation.MULTIPLY_TOTAL);
        }
    },
    SLOW_FALLING((byte) 28) {
        @Override
        public AttributeModifier getModifier(int amplifier, double duration) {
            return new AttributeModifier(UUID.fromString("91AEAA56-376B-4498-935B-2F7F68070635"),
                    "potion.moveSpeed", 0.2f * (amplifier + 1),
                    AttributeOperation.MULTIPLY_TOTAL);
        }
    },
    CONDUIT_POWER((byte) 9) {
        @Override
        public AttributeModifier getModifier(int amplifier, double duration) {
            return new AttributeModifier(UUID.fromString("91AEAA56-376B-4498-935B-2F7F68070635"),
                    "potion.moveSpeed", 0.2f * (amplifier + 1),
                    AttributeOperation.MULTIPLY_TOTAL);
        }
    },
    DOLPHINS_GRACE((byte) 30) {
        @Override
        public AttributeModifier getModifier(int amplifier, double duration) {
            return new AttributeModifier(UUID.fromString("91AEAA56-376B-4498-935B-2F7F68070635"),
                    "potion.moveSpeed", 0.2f * (amplifier + 1),
                    AttributeOperation.MULTIPLY_TOTAL);
        }
    },
    BAD_OMEN((byte) 31) {
        @Override
        public AttributeModifier getModifier(int amplifier, double duration) {
            return new AttributeModifier(UUID.fromString("91AEAA56-376B-4498-935B-2F7F68070635"),
                    "potion.moveSpeed", 0.2f * (amplifier + 1),
                    AttributeOperation.MULTIPLY_TOTAL);
        }
    },
    HERO_OF_THE_VILLAGE((byte) 32) {
        @Override
        public AttributeModifier getModifier(int amplifier, double duration) {
            return new AttributeModifier(UUID.fromString("91AEAA56-376B-4498-935B-2F7F68070635"),
                    "potion.moveSpeed", 0.2f * (amplifier + 1),
                    AttributeOperation.MULTIPLY_TOTAL);
        }
    };

    private final byte id;

    public abstract AttributeModifier getModifier(int amplifier, double duration);

    public AttributeModifier getModifier(int amplifier) {
        return getModifier(amplifier, 0);
    }
}
