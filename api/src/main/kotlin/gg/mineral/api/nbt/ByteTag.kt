package gg.mineral.api.nbt

/**
 * The `TAG_Byte` tag.
 */
class ByteTag
/**
 * Creates the tag.
 *
 * @param value The value.
 */(
    /**
     * The value.
     */
    override val value: Byte
) : Tag<Byte>(TagType.BYTE)
