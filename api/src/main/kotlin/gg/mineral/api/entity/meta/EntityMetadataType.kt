package gg.mineral.api.entity.meta

import gg.mineral.api.inventory.item.ItemStack
import gg.mineral.api.math.EulerAngle
import java.util.*

/**
 * The types of values that entity metadata can contain.
 */
enum class EntityMetadataType(val dataType: Class<*>) {
    BYTE(Byte::class.java),
    SHORT(Short::class.java),
    INT(Int::class.java),
    FLOAT(Float::class.java),
    STRING(String::class.java),
    ITEM(ItemStack::class.java),
    VECTOR(Vector::class.java),
    EULER_ANGLE(EulerAngle::class.java);

    val id: Byte
        get() = ordinal.toByte()

    companion object {
        @JvmStatic
        fun byId(id: Int): EntityMetadataType {
            return entries[id]
        }
    }
}