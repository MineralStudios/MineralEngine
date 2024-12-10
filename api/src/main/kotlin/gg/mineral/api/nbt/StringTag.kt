package gg.mineral.api.nbt

/**
 * The `TAG_String` tag.
 */
internal class StringTag
/**
 * Creates the tag.
 *
 * @param value The value.
 */(
    /**
     * The value.
     */
    override val value: String
) : Tag<String>(TagType.STRING)
