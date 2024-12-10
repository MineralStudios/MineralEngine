package gg.mineral.api.nbt

/**
 * The `TAG_Byte_Array` tag.
 */
class ByteArrayTag
/**
 * Creates the tag.
 *
 * @param value The value.
 */(
    /**
     * The value.
     */
    override vararg val value: Byte
) : Tag<ByteArray>(TagType.BYTE_ARRAY) {

    override fun valueToString(builder: StringBuilder) {
        for (b in value) {
            val hexDigits = Integer.toHexString(b.toInt() and 0xff)
            val len = 1
            if (hexDigits.length == len) builder.append("0")

            builder.append(hexDigits).append(" ")
        }
    }
}
