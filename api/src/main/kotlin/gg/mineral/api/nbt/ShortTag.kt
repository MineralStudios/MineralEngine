package gg.mineral.api.nbt

/**
 * The `TAG_Short` tag.
 */
class ShortTag
/**
 * Creates the tag.
 *
 * @param value The value.
 */(
    /**
     * The value.
     */
    override val value: Short
) : Tag<Short>(TagType.SHORT)
