package gg.mineral.api.nbt;

import lombok.val;

/**
 * The {@code TAG_Byte_Array} tag.
 */
public final class ByteArrayTag extends Tag<byte[]> {

    /**
     * The value.
     */
    private final byte[] value;

    /**
     * Creates the tag.
     *
     * @param value The value.
     */
    public ByteArrayTag(byte... value) {
        super(TagType.BYTE_ARRAY);
        this.value = value;
    }

    @Override
    public byte[] getValue() {
        return value;
    }

    @Override
    protected void valueToString(StringBuilder hex) {
        for (byte b : value) {
            val hexDigits = Integer.toHexString(b & 0xff);
            int len = 1;
            if (hexDigits.length() == len)
                hex.append("0");

            hex.append(hexDigits).append(" ");
        }
    }

}
