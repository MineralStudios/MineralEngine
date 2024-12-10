package gg.mineral.api.nbt

import java.io.Closeable
import java.io.DataOutputStream
import java.io.IOException
import java.io.OutputStream
import java.nio.charset.StandardCharsets
import java.util.zip.GZIPOutputStream

/**
 * This class writes NBT, or Named Binary Tag, [Tag] objects to an
 * underlying [OutputStream].
 *
 *
 * The NBT format was created by Markus Persson, and the specification may
 * be found at [
 * http://www.minecraft.net/docs/NBT.txt](http://www.minecraft.net/docs/NBT.txt).
 */
class NBTOutputStream @JvmOverloads constructor(os: OutputStream, compressed: Boolean = true) :
    Closeable {
    /**
     * The output stream.
     */
    private val os = DataOutputStream(if (compressed) GZIPOutputStream(os) else os)

    /**
     * Creates a new NBTOutputStream, which will write data to the
     * specified underlying output stream. A flag indicates if the output
     * should be compressed with GZIP or not.
     *
     * @param os         The output stream.
     * @param compressed A flag that indicates if the output should be compressed.
     * @throws IOException if an I/O error occurs.
     */

    /**
     * Write a tag with a blank name (the root tag) to the stream.
     *
     * @param tag The tag to write.
     * @throws IOException if an I/O error occurs.
     */
    @Throws(IOException::class)
    fun writeTag(tag: Tag<*>) {
        writeTag("", tag)
    }

    /**
     * Write a tag with a name.
     *
     * @param name The name to give the written tag.
     * @param tag  The tag to write.
     * @throws IOException if an I/O error occurs.
     */
    @Throws(IOException::class)
    private fun writeTag(name: String, tag: Tag<*>) {
        val type = tag.type
        val nameBytes = name.toByteArray(StandardCharsets.UTF_8)

        if (type === TagType.END) throw IOException("Named TAG_End not permitted.")

        os.writeByte(type.id.toInt())
        os.writeShort(nameBytes.size)
        os.write(nameBytes)

        writeTagPayload(tag)
    }

    /**
     * Writes tag payload.
     *
     * @param tag The tag.
     * @throws IOException if an I/O error occurs.
     */
    @Throws(IOException::class)
    private fun writeTagPayload(tag: Tag<*>) {
        if (tag.value == null) {
            throw IOException("Null tag value.")
        }

        when (tag) {
            is ByteTag -> os.writeByte(tag.value.toInt())
            is ShortTag -> os.writeShort(tag.value.toInt())
            is IntTag -> os.writeInt(tag.value)
            is LongTag -> os.writeLong(tag.value)
            is FloatTag -> os.writeFloat(tag.value)
            is DoubleTag -> os.writeDouble(tag.value)
            is ByteArrayTag -> {
                val bytes = tag.value
                os.writeInt(bytes.size)
                os.write(bytes)
            }

            is StringTag -> {
                val bytes = tag.value.toByteArray(StandardCharsets.UTF_8)
                os.writeShort(bytes.size)
                os.write(bytes)
            }

            is ListTag<*> -> {
                val tags = tag.value
                os.writeByte(tag.childType.id.toInt())
                os.writeInt(tags.size)
                for (child in tags)
                    writeTagPayload(child)
            }

            is CompoundTag -> {
                val map = tag.value
                for ((key, value) in map)
                    writeTag(key, value)

                os.writeByte(0) // end tag
            }

            is IntArrayTag -> {
                val ints = tag.value
                os.writeInt(ints.size)
                for (v in ints)
                    os.writeInt(v)
            }

            else -> throw IOException("Invalid tag type: $tag.")
        }
    }

    @Throws(IOException::class)
    override fun close() {
        os.close()
    }
}
