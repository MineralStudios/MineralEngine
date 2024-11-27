package gg.mineral.api.nbt;

import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

import lombok.NonNull;
import lombok.val;

/**
 * This class writes NBT, or Named Binary Tag, {@link Tag} objects to an
 * underlying {@link OutputStream}.
 * <p>
 * The NBT format was created by Markus Persson, and the specification may
 * be found at <a href="http://www.minecraft.net/docs/NBT.txt">
 * http://www.minecraft.net/docs/NBT.txt</a>.
 */
public final class NBTOutputStream implements Closeable {

    /**
     * The output stream.
     */
    private final DataOutputStream os;

    /**
     * Creates a new NBTOutputStream, which will write data to the
     * specified underlying output stream. This assumes the output stream
     * should be compressed with GZIP.
     *
     * @param os The output stream.
     * @throws IOException if an I/O error occurs.
     */
    public NBTOutputStream(OutputStream os) throws IOException {
        this(os, true);
    }

    /**
     * Creates a new NBTOutputStream, which will write data to the
     * specified underlying output stream. A flag indicates if the output
     * should be compressed with GZIP or not.
     *
     * @param os         The output stream.
     * @param compressed A flag that indicates if the output should be compressed.
     * @throws IOException if an I/O error occurs.
     */
    public NBTOutputStream(OutputStream os, boolean compressed) throws IOException {
        this.os = new DataOutputStream(compressed ? new GZIPOutputStream(os) : os);
    }

    /**
     * Write a tag with a blank name (the root tag) to the stream.
     *
     * @param tag The tag to write.
     * @throws IOException if an I/O error occurs.
     */
    public void writeTag(Tag<?> tag) throws IOException {
        writeTag("", tag);
    }

    /**
     * Write a tag with a name.
     *
     * @param name The name to give the written tag.
     * @param tag  The tag to write.
     * @throws IOException if an I/O error occurs.
     */
    private void writeTag(String name, Tag<?> tag) throws IOException {
        val type = tag.getType();
        val nameBytes = name.getBytes(StandardCharsets.UTF_8);

        if (type == TagType.END)
            throw new IOException("Named TAG_End not permitted.");

        os.writeByte(type.getId());
        os.writeShort(nameBytes.length);
        os.write(nameBytes);

        writeTagPayload(tag);
    }

    /**
     * Writes tag payload.
     *
     * @param tag The tag.
     * @throws IOException if an I/O error occurs.
     */
    private void writeTagPayload(@NonNull Tag<?> tag) throws IOException {

        if (tag.getValue() == null)
            throw new IOException("Null tag value.");

        switch (tag) {
            case ByteTag byteTag ->
                os.writeByte(byteTag.getValue());
            case ShortTag shortTag ->
                os.writeShort(shortTag.getValue());
            case IntTag intTag ->
                os.writeInt(intTag.getValue());
            case LongTag longTag ->
                os.writeLong(longTag.getValue());
            case FloatTag floatTag ->
                os.writeFloat(floatTag.getValue());
            case DoubleTag doubleTag ->
                os.writeDouble(doubleTag.getValue());
            case ByteArrayTag byteArrayTag -> {
                val bytes = byteArrayTag.getValue();
                os.writeInt(bytes.length);
                os.write(bytes);
            }
            case StringTag stringTag -> {
                val bytes = stringTag.getValue().getBytes(StandardCharsets.UTF_8);
                os.writeShort(bytes.length);
                os.write(bytes);
            }
            case ListTag<?> listTag -> {
                val tags = listTag.getValue();
                os.writeByte(listTag.getChildType().getId());
                os.writeInt(tags.size());
                for (val child : tags)
                    writeTagPayload(child);
            }
            case CompoundTag compoundTag -> {
                val map = compoundTag.getValue();
                for (val entry : map.entrySet())
                    writeTag(entry.getKey(), entry.getValue());

                os.writeByte((byte) 0); // end tag
            }
            case IntArrayTag intArrayTag -> {
                val ints = intArrayTag.getValue();
                os.writeInt(ints.length);
                for (int v : ints)
                    os.writeInt(v);
            }
            default ->
                throw new IOException("Invalid tag type: " + tag + ".");
        }
    }

    @Override
    public void close() throws IOException {
        os.close();
    }

}
