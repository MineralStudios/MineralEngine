package gg.mineral.api.nbt

import java.io.IOException

/**
 * The types of NBT tags that exist.
 */
enum class TagType(override val name: String, tagClass: Class<T?>, valueClass: Class<V?>) {
    END("End", null, Void::class.java) {
        override fun newObj(type: Any): Tag<*>? {
            return null
        }
    },
    BYTE("Byte", ByteTag::class.java, Byte::class.javaPrimitiveType!!) {
        override fun newObj(type: Any): Tag<*> {
            return ByteTag(type as Byte)
        }
    },
    SHORT("Short", ShortTag::class.java, Short::class.javaPrimitiveType!!) {
        override fun newObj(type: Any): Tag<*> {
            return ShortTag(type as Short)
        }
    },
    INT("Int", IntTag::class.java, Int::class.javaPrimitiveType!!) {
        override fun newObj(type: Any): Tag<*> {
            return IntTag(type as Int)
        }
    },
    LONG("Long", LongTag::class.java, Long::class.javaPrimitiveType!!) {
        override fun newObj(type: Any): Tag<*> {
            return LongTag(type as Long)
        }
    },
    FLOAT("Float", FloatTag::class.java, Float::class.javaPrimitiveType!!) {
        override fun newObj(type: Any): Tag<*> {
            return FloatTag(type as Float)
        }
    },
    DOUBLE("Double", DoubleTag::class.java, Double::class.javaPrimitiveType!!) {
        override fun newObj(type: Any): Tag<*> {
            return DoubleTag(type as Double)
        }
    },
    BYTE_ARRAY("Byte_Array", ByteArrayTag::class.java, ByteArray::class.java) {
        override fun newObj(type: Any): Tag<*> {
            return ByteArrayTag(*type as ByteArray)
        }
    },
    STRING("String", StringTag::class.java, String::class.java) {
        override fun newObj(type: Any): Tag<*> {
            return StringTag((type as String))
        }
    },

    // javac complains about this because ListTag is generic
    LIST("List", ListTag::class.java, MutableList::class.java) {
        override fun newObj(type: Any): Tag<*>? {
            return null
        }
    },
    COMPOUND("Compound", CompoundTag::class.java, MutableMap::class.java) {
        override fun newObj(type: Any): Tag<*> {
            return CompoundTag()
        }
    },
    INT_ARRAY("Int_Array", IntArrayTag::class.java, IntArray::class.java) {
        override fun newObj(type: Any): Tag<*> {
            return IntArrayTag(*type as IntArray)
        }
    };

    // ? extends V is needed to get Compound to work for some reason
    val tagClass: Class<out Tag<*>?> = tagClass
    val valueClass: Class<*> = valueClass

    val id: Byte
        get() = ordinal.toByte()

    abstract fun newObj(type: Any): Tag<*>?

    companion object {
        fun byId(id: Int): TagType? {
            if (id < 0 || id >= entries.toTypedArray().length) return null
            return entries[id]
        }

        @Throws(IOException::class)
        fun byIdOrError(id: Int): TagType {
            if (id < 0 || id >= entries.toTypedArray().length) throw IOException("Invalid tag type: $id")
            return entries[id]
        }
    }
}
