package gg.mineral.api.nbt

/**
 * The `TAG_Double` tag.
 */
internal class DoubleTag
/**
 * Creates the tag.
 *
 * @param value The value.
 */(
    /**
     * The value.
     */
    override val value: Double
) : Tag<Double>(TagType.DOUBLE)
