package gg.mineral.api.nbt

import lombok.AccessLevel
import lombok.Getter
import lombok.RequiredArgsConstructor

/**
 * Represents a single NBT tag.
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
abstract class Tag<T> {
    /**
     * The type of this tag.
     */
    private val type: TagType? = null

    /**
     * Gets the value of this tag.
     *
     * @return The value of this tag.
     */
    abstract val value: T

    override fun toString(): String {
        val builder = StringBuilder("TAG_")
        builder.append(type!!.getName()).append(": ")
        valueToString(builder)
        return builder.toString()
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val tag = o as Tag<*>

        val value: T? = value

        return type === tag.type && value != null && value == tag.value
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        val value: T? = value
        if (value != null) result = 31 * result + value.hashCode()
        return result
    }

    protected open fun valueToString(builder: StringBuilder) {
        builder.append(value)
    }
}
