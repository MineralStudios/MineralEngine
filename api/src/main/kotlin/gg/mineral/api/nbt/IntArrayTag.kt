package gg.mineral.api.nbt

/**
 * The `TAG_Int_Array` tag.
 */
internal class IntArrayTag
/**
 * Creates the tag.
 *
 * @param value The value.
 */(
    /**
     * The value.
     */
    override vararg val value: Int
) : Tag<IntArray>(TagType.INT_ARRAY) {
    override fun valueToString(builder: StringBuilder) {
        for (b in value) {
            val hexDigits = Integer.toHexString(b)
            builder.append("00000000", hexDigits.length, 8)
            builder.append(hexDigits).append(" ")
        }
    }
}
