package gg.mineral.api.nbt

import java.io.IOException

/**
 * The types of NBT tags that exist.
 */
enum class TagType(val typeName: String, val tagClass: Class<out Tag<*>>?, val valueClass: Class<*>) {
    END("End", null, Void::class.java) {
        override fun newObj(type: Any): Tag<*>? {
            return null
        }
    },
    BYTE("Byte", ByteTag::class.java, Byte::class.javaPrimitiveType!!) {
        override fun newObj(type: Any): Tag<*> = ByteTag(type as Byte)
    },
    SHORT("Short", ShortTag::class.java, Short::class.javaPrimitiveType!!) {
        override fun newObj(type: Any): Tag<*> = ShortTag(type as Short)
    },
    INT("Int", IntTag::class.java, Int::class.javaPrimitiveType!!) {
        override fun newObj(type: Any): Tag<*> = IntTag(type as Int)
    },
    LONG("Long", LongTag::class.java, Long::class.javaPrimitiveType!!) {
        override fun newObj(type: Any): Tag<*> = LongTag(type as Long)
    },
    FLOAT("Float", FloatTag::class.java, Float::class.javaPrimitiveType!!) {
        override fun newObj(type: Any): Tag<*> = FloatTag(type as Float)
    },
    DOUBLE("Double", DoubleTag::class.java, Double::class.javaPrimitiveType!!) {
        override fun newObj(type: Any): Tag<*> = DoubleTag(type as Double)
    },
    BYTE_ARRAY("Byte_Array", ByteArrayTag::class.java, ByteArray::class.java) {
        override fun newObj(type: Any): Tag<*> = ByteArrayTag(*type as ByteArray)
    },
    STRING("String", StringTag::class.java, String::class.java) {
        override fun newObj(type: Any): Tag<*> = StringTag((type as String))
    },

    // javac complains about this because ListTag is generic
    LIST("List", ListTag::class.java, MutableList::class.java) {
        override fun newObj(type: Any): Tag<*>? = null
    },
    COMPOUND("Compound", CompoundTag::class.java, MutableMap::class.java) {
        override fun newObj(type: Any): Tag<*> = CompoundTag()
    },
    INT_ARRAY("Int_Array", IntArrayTag::class.java, IntArray::class.java) {
        override fun newObj(type: Any): Tag<*> = IntArrayTag(*type as IntArray)
    };

    val id: Byte
        get() = ordinal.toByte()

    abstract fun newObj(type: Any): Tag<*>?

    companion object {
        fun byId(id: Int): TagType? {
            if (id < 0 || id >= entries.toTypedArray().size) return null
            return entries[id]
        }

        @Throws(IOException::class)
        fun byIdOrError(id: Int): TagType {
            if (id < 0 || id >= entries.toTypedArray().size) throw IOException("Invalid tag type: $id")
            return entries[id]
        }
    }
}
