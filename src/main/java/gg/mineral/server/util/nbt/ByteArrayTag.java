package gg.mineral.server.util.nbt;

import lombok.val;

/**
 * The {@code TAG_Byte_Array} tag.
 */
final class ByteArrayTag extends Tag<byte[]> {

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
            if (hexDigits.length() == 1)
                hex.append("0");

            hex.append(hexDigits).append(" ");
        }
    }

}
