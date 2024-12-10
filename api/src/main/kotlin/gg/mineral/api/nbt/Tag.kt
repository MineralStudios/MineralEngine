package gg.mineral.api.nbt

/**
 * Represents a single NBT tag.
 */
abstract class Tag<T>(val type: TagType) {
    /**
     * Gets the value of this tag.
     *
     * @return The value of this tag.
     */
    abstract val value: T

    override fun toString(): String {
        val builder = StringBuilder("TAG_")
        builder.append(type.name).append(": ")
        valueToString(builder)
        return builder.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val tag = other as Tag<*>

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
