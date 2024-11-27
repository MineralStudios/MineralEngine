package gg.mineral.api.nbt;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * The types of NBT tags that exist.
 */
public enum TagType {

    END("End", null, Void.class) {
        @Override
        public Tag<?> newObj(Object type) {
            return null;
        }
    },
    BYTE("Byte", ByteTag.class, byte.class) {
        @Override
        public Tag<?> newObj(Object type) {
            return new ByteTag((byte) type);
        }
    },
    SHORT("Short", ShortTag.class, short.class) {
        @Override
        public Tag<?> newObj(Object type) {
            return new ShortTag((short) type);
        }
    },
    INT("Int", IntTag.class, int.class) {
        @Override
        public Tag<?> newObj(Object type) {
            return new IntTag((int) type);
        }
    },
    LONG("Long", LongTag.class, long.class) {
        @Override
        public Tag<?> newObj(Object type) {
            return new LongTag((long) type);
        }
    },
    FLOAT("Float", FloatTag.class, float.class) {
        @Override
        public Tag<?> newObj(Object type) {
            return new FloatTag((float) type);
        }
    },
    DOUBLE("Double", DoubleTag.class, double.class) {
        @Override
        public Tag<?> newObj(Object type) {
            return new DoubleTag((double) type);
        }
    },
    BYTE_ARRAY("Byte_Array", ByteArrayTag.class, byte[].class) {
        @Override
        public Tag<?> newObj(Object type) {
            return new ByteArrayTag((byte[]) type);
        }
    },
    STRING("String", StringTag.class, String.class) {
        @Override
        public Tag<?> newObj(Object type) {
            return new StringTag((String) type);
        }
    },
    // javac complains about this because ListTag is generic
    @SuppressWarnings("unchecked")
    LIST("List", ListTag.class, List.class) {
        @Override
        public Tag<?> newObj(Object type) {
            return null;
        }
    },
    COMPOUND("Compound", CompoundTag.class, Map.class) {
        @Override
        public Tag<?> newObj(Object type) {
            return new CompoundTag();
        }
    },
    INT_ARRAY("Int_Array", IntArrayTag.class, int[].class) {
        @Override
        public Tag<?> newObj(Object type) {
            return new IntArrayTag((int[]) type);
        }
    };

    private final String name;
    private final Class<? extends Tag<?>> tagClass;
    private final Class<?> valueClass;

    <V, T extends Tag<? extends V>> TagType(String name, Class<T> tagClass, Class<V> valueClass) {
        // ? extends V is needed to get Compound to work for some reason
        this.name = name;
        this.tagClass = tagClass;
        this.valueClass = valueClass;
    }

    public static TagType byId(int id) {
        if (id < 0 || id >= values().length)
            return null;
        return values()[id];
    }

    static TagType byIdOrError(int id) throws IOException {
        if (id < 0 || id >= values().length)
            throw new IOException("Invalid tag type: " + id);
        return values()[id];
    }

    public byte getId() {
        return (byte) ordinal();
    }

    public String getName() {
        return name;
    }

    public Class<? extends Tag<?>> getTagClass() {
        return tagClass;
    }

    public Class<?> getValueClass() {
        return valueClass;
    }

    public abstract Tag<?> newObj(Object type);
}
