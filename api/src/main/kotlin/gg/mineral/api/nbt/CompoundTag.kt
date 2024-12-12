package gg.mineral.api.nbt

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap
import java.util.stream.Collectors

/**
 * The `TAG_Compound` tag.
 */
class CompoundTag
/**
 * Creates a new, empty CompoundTag.
 */
    : Tag<Map<String, Tag<*>>>(TagType.COMPOUND) {
    /**
     * The value.
     */
    override val value: MutableMap<String, Tag<*>> = Object2ObjectLinkedOpenHashMap()

    override fun valueToString(builder: StringBuilder) {
        builder.append(value.size).append(" entries\n{\n")
        for ((key, value) in value) {
            builder.append("    ")
                .append(key)
                .append(": ")
                .append(value.toString().replace("\n", "\n    "))
                .append("\n")
        }
        builder.append("}")
    }

    val isEmpty: Boolean
        /**/
        get() = value.isEmpty()

    /**
     * Check if the compound contains the given key.
     *
     * @param key The key.
     * @return True if the key is in the map.
     */
    fun containsKey(key: String): Boolean = value.containsKey(key)

    fun remove(key: String) =
        value.remove(key)

    /**///////////////////////////////////////////////////////////////////////// */ // Simple gets
    fun getBool(key: String): Boolean = get(key, ByteTag::class.java)!!.toInt() != 0


    fun getByte(key: String): Byte {
        val tag = get(key, ByteTag::class.java)
        return tag ?: 0
    }

    fun getShort(key: String): Short {
        val tag = get(key, ShortTag::class.java)
        return tag ?: 0
    }

    fun getInt(key: String): Int {
        val tag = get(key, IntTag::class.java)
        return tag ?: 0
    }

    fun getLong(key: String): Long {
        val tag = get(key, LongTag::class.java)
        return tag ?: 0
    }

    fun getFloat(key: String): Float {
        val tag = get(key, FloatTag::class.java)
        return tag ?: 0f
    }

    fun getDouble(key: String): Double {
        val tag = get(key, DoubleTag::class.java)
        return tag ?: 0.0
    }

    fun getByteArray(key: String): ByteArray = get(key, ByteArrayTag::class.java)!!

    fun getString(key: String): String? = get(key, StringTag::class.java)

    fun getIntArray(key: String): IntArray = get(key, IntArrayTag::class.java)!!

    /**///////////////////////////////////////////////////////////////////////// */ // Fancy gets
    fun <V> getList(key: String, type: TagType): List<V> {
        val original = getTagList(key, type)
        val result: MutableList<V> = ArrayList(original.size)
        result.addAll(
            original.stream().map { item: Tag<*> -> item.value as V }
                .collect(Collectors.toList()))
        return result
    }

    fun getCompound(key: String) = getTag(key, CompoundTag::class.java)

    fun getCompoundList(key: String) = getTagList(key, TagType.COMPOUND) as List<CompoundTag>

    /**///////////////////////////////////////////////////////////////////////// */ // Simple is
    fun isByte(key: String) = `is`(key, ByteTag::class.java)

    fun isShort(key: String) = `is`(key, ShortTag::class.java)

    fun isInt(key: String) = `is`(key, IntTag::class.java)

    fun isLong(key: String) = `is`(key, LongTag::class.java)

    fun isFloat(key: String) = `is`(key, FloatTag::class.java)

    fun isDouble(key: String) = `is`(key, DoubleTag::class.java)

    fun isByteArray(key: String) = `is`(key, ByteArrayTag::class.java)

    fun isString(key: String) = `is`(key, StringTag::class.java)

    fun isIntArray(key: String) = `is`(key, IntArrayTag::class.java)

    /**///////////////////////////////////////////////////////////////////////// */ // Fancy is
    fun isList(key: String, type: TagType): Boolean {
        if (!`is`(key, ListTag::class.java)) return false
        val tag = getTag(key, ListTag::class.java)
        return tag.childType === type
    }

    fun isCompound(key: String) = `is`(key, CompoundTag::class.java)

    /**///////////////////////////////////////////////////////////////////////// */ // Simple sets
    fun putBool(key: String, value: Boolean) =
        putByte(key, if (value) 1 else 0)

    fun putByte(key: String, value: Int) =
        put(key, ByteTag(value.toByte()))

    fun putShort(key: String, value: Int) =
        put(key, ShortTag(value.toShort()))

    fun putInt(key: String, value: Int) =
        put(key, IntTag(value))

    fun putLong(key: String, value: Long) =
        put(key, LongTag(value))

    fun putFloat(key: String, value: Double) =
        put(key, FloatTag(value.toFloat()))

    fun putDouble(key: String, value: Double) =
        put(key, DoubleTag(value))

    fun putByteArray(key: String, vararg value: Byte) =
        put(key, ByteArrayTag(*value))

    fun putString(key: String, value: String) =
        put(key, StringTag(value))

    fun putIntArray(key: String, vararg value: Int) =
        put(key, IntArrayTag(*value))

    /**///////////////////////////////////////////////////////////////////////// */ // Fancy sets
    fun <V : Any> putList(key: String, type: TagType, value: List<V>) {
        val result: MutableList<Tag<*>> = ArrayList(value.size)
        for (item in value) {
            val tagObj = type.newObj(item)
            if (tagObj != null)
                result.add(tagObj)
        }

        put(key, ListTag(type, result))
    }

    fun putCompound(key: String, tag: CompoundTag) =
        put(key, tag)

    fun putCompoundList(key: String, list: List<CompoundTag>) =
        put(key, ListTag(TagType.COMPOUND, list))

    /**///////////////////////////////////////////////////////////////////////// */ // Accessor helpers
    private fun <T : Tag<*>?> `is`(key: String, clazz: Class<T>): Boolean {
        if (!containsKey(key)) return false
        val tag = value[key]
        return tag != null && clazz == tag.javaClass
    }

    fun put(key: String, tag: Tag<*>) {
        value[key] = tag
    }

    private fun <V, T : Tag<V>?> get(key: String, clazz: Class<T>): V? {
        val tag = getTag(key, clazz)
        return tag?.value ?: null as V?
    }

    fun <T : Tag<*>?> getTag(key: String, clazz: Class<T>): T {
        require(`is`(key, clazz)) { "Compound does not contain " + clazz.simpleName + " \"" + key + "\"" }

        return value[key] as T
    }

    private fun getTagList(key: String, type: TagType): List<Tag<*>> {
        val tag = getTag(key, ListTag::class.java)
        if (tag.value.isEmpty())
        // empty lists are allowed to be the wrong type
            return mutableListOf()

        require(tag.childType === type) { "List \"" + key + "\" contains " + tag.childType + ", not " + type }
        return tag.value
    }
}
