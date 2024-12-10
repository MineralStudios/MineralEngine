package gg.mineral.server.entity.meta

import gg.mineral.api.entity.meta.EntityMetadata
import gg.mineral.api.entity.meta.EntityMetadataIndex
import gg.mineral.api.entity.meta.EntityMetadataType
import gg.mineral.api.inventory.item.ItemStack
import gg.mineral.server.entity.EntityImpl
import java.util.*
import java.util.stream.Collectors

/**
 * A map for entity metadata.
 */
class EntityMetadataImpl(private val entityClass: Class<out EntityImpl?>) : EntityMetadata {
    private val map: MutableMap<EntityMetadataIndex, Any> = EnumMap(
        EntityMetadataIndex::class.java
    )
    private val changes: MutableList<EntityMetadata.Entry> = ArrayList(4)

    init {
        set(EntityMetadataIndex.STATUS, 0.toByte()) // all entities have to have at least this
    }

    fun containsKey(index: EntityMetadataIndex): Boolean {
        return map.containsKey(index)
    }

    fun set(index: EntityMetadataIndex, value: Any) {
        val adjustedValue: Any = if (value is Number) {
            when (index.type) {
                EntityMetadataType.BYTE -> value.toByte()
                EntityMetadataType.SHORT -> value.toShort()
                EntityMetadataType.INT -> value.toInt()
                EntityMetadataType.FLOAT -> value.toFloat()
                else -> value
            }
        } else {
            value
        }

        // Helper function to get boxed type
        fun Class<*>.boxed(): Class<*> {
            return when (this) {
                java.lang.Byte.TYPE -> java.lang.Byte::class.java
                java.lang.Short.TYPE -> java.lang.Short::class.java
                java.lang.Integer.TYPE -> java.lang.Integer::class.java
                java.lang.Long.TYPE -> java.lang.Long::class.java
                java.lang.Float.TYPE -> java.lang.Float::class.java
                java.lang.Double.TYPE -> java.lang.Double::class.java
                java.lang.Boolean.TYPE -> java.lang.Boolean::class.java
                java.lang.Character.TYPE -> java.lang.Character::class.java
                else -> this
            }
        }

        val expectedType = if (index.type.dataType.isPrimitive) {
            index.type.dataType.boxed()
        } else {
            index.type.dataType
        }

        require(expectedType.isAssignableFrom(adjustedValue.javaClass)) {
            "Cannot assign $adjustedValue to $index, expects ${index.type.dataType.simpleName}"
        }

        require(index.appliesTo(entityClass)) {
            "Index $index does not apply to ${entityClass.simpleName}, only ${index.appliesTo.simpleName}"
        }

        val prev = map.put(index, adjustedValue)
        if (prev != adjustedValue) changes.add(EntityMetadata.Entry(index, adjustedValue))
    }

    fun get(index: EntityMetadataIndex): Any? {
        return map[index]
    }

    fun getBit(index: EntityMetadataIndex, bit: Int): Boolean {
        return (getNumber(index).toInt() and bit) != 0
    }

    fun setBit(index: EntityMetadataIndex, bit: Int, status: Boolean) {
        if (status) set(index, getNumber(index).toInt() or bit)
        else set(index, getNumber(index).toInt() and bit.inv())
    }

    fun getNumber(index: EntityMetadataIndex): Number {
        if (!containsKey(index)) return 0

        val o = get(index)
        require(o is Number) { "Index " + index + " is of non-number type " + index.type }

        return o
    }

    fun getByte(index: EntityMetadataIndex): Byte {
        return get(index, EntityMetadataType.BYTE, 0.toByte())
    }

    fun getShort(index: EntityMetadataIndex): Short {
        return get(index, EntityMetadataType.SHORT, 0.toShort())
    }

    fun getInt(index: EntityMetadataIndex): Int {
        return get(index, EntityMetadataType.INT, 0)
    }

    fun getFloat(index: EntityMetadataIndex): Float {
        return get(index, EntityMetadataType.FLOAT, 0f)
    }

    fun getString(index: EntityMetadataIndex): String? {
        return get<String?>(index, EntityMetadataType.STRING, null)
    }

    fun getItem(index: EntityMetadataIndex): ItemStack? {
        return get<ItemStack?>(index, EntityMetadataType.ITEM, null)
    }

    private fun <T> get(index: EntityMetadataIndex, expected: EntityMetadataType, def: T): T {
        require(index.type == expected) { "Cannot get " + index + ": is " + index.type + ", not " + expected }

        val t = map[index] as T?
        return t ?: def
    }

    val entryList: List<EntityMetadata.Entry>
        get() {
            val result =
                ArrayList<EntityMetadata.Entry>(map.size)
            result.addAll(
                map.entries.stream()
                    .map { entry: Map.Entry<EntityMetadataIndex, Any> ->
                        EntityMetadata.Entry(
                            entry.key,
                            entry.value
                        )
                    }
                    .collect(
                        Collectors.toList()
                    ))
            result.sort()
            return result
        }

    fun getChanges(): List<EntityMetadata.Entry> {
        changes.sort()
        return ArrayList(changes)
    }

    fun resetChanges() {
        changes.clear()
    }
}