package gg.mineral.api.nbt

/**
 * The `TAG_Long` tag.
 */
internal class LongTag
/**
 * Creates the tag.
 *
 * @param value The value.
 */(
    /**
     * The value.
     */
    override val value: Long
) : Tag<Long?>(TagType.LONG)
