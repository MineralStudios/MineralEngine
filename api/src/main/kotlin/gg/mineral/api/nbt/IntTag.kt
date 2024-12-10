package gg.mineral.api.nbt

/**
 * The `TAG_Int` tag.
 */
internal class IntTag
/**
 * Creates the tag.
 *
 * @param value The value.
 */(
    /**
     * The value.
     */
    override val value: Int
) : Tag<Int>(TagType.INT)
