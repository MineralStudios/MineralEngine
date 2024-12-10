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
) : Tag<IntArray?>(TagType.INT_ARRAY) {
    override fun valueToString(hex: StringBuilder) {
        for (b in value) {
            val hexDigits = Integer.toHexString(b)
            hex.append("00000000", hexDigits.length, 8)
            hex.append(hexDigits).append(" ")
        }
    }
}
