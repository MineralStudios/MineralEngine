package gg.mineral.api.nbt

/**
 * The `TAG_Float` tag.
 */
internal class FloatTag
/**
 * Creates the tag.
 *
 * @param value The value.
 */(
    /**
     * The value.
     */
    override val value: Float
) : Tag<Float>(TagType.FLOAT)
