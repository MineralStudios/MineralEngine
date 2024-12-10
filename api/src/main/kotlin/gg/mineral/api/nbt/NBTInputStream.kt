package gg.mineral.api.nbt

import java.io.Closeable
import java.io.DataInputStream
import java.io.IOException
import java.io.InputStream
import java.util.zip.GZIPInputStream

/**
 * This class reads NBT, or Named Binary Tag streams, and produces an object
 * graph of subclasses of the [Tag] object.
 *
 *
 * The NBT format was created by Markus Persson, and the specification may
 * be found at [
 * http://www.minecraft.net/docs/NBT.txt](http://www.minecraft.net/docs/NBT.txt).
 */
class NBTInputStream @JvmOverloads constructor(`is`: InputStream, compressed: Boolean = true) :
    Closeable {
    /**
     * The data input stream.
     */
    private val `is` = DataInputStream(if (compressed) GZIPInputStream(`is`) else `is`)

    /**
     * Creates a new NBTInputStream, which sources its data from the
     * specified input stream. A flag must be passed which indicates if the
     * stream is compressed with GZIP or not.
     *
     * @param is         The input stream.
     * @param compressed A flag indicating if the stream is compressed.
     * @throws IOException if an I/O error occurs.
     */

    /**
     * Reads the root NBT [CompoundTag] from the stream.
     *
     * @return The tag that was read.
     * @throws IOException if an I/O error occurs.
     */
    /**
     * Reads the root NBT [CompoundTag] from the stream.
     *
     * @return The tag that was read.
     * @throws IOException if an I/O error occurs.
     */
    @JvmOverloads
    @Throws(IOException::class)
    fun readCompound(readLimiter: NBTReadLimiter = NBTReadLimiter.UNLIMITED): CompoundTag {
        // read type
        val type = TagType.byIdOrError(`is`.readUnsignedByte())
        if (type !== TagType.COMPOUND) {
            throw IOException("Root of NBTInputStream was $type, not COMPOUND")
        }

        // for now, throw away name
        val nameLength = `is`.readUnsignedShort()
        `is`.skipBytes(nameLength)

        // read tag
        return readTagPayload(type, 0, readLimiter) as CompoundTag
    }

    @Throws(IOException::class)
    private fun readCompound(depth: Int, readLimiter: NBTReadLimiter): CompoundTag {
        val result = CompoundTag()

        while (true) {
            // read type
            val type = TagType.byIdOrError(`is`.readUnsignedByte())
            if (type === TagType.END) break

            // read name
            val name = `is`.readUTF()
            readLimiter.read(28 + 2 * name.length)

            // read tag
            val tag = readTagPayload(type, depth + 1, readLimiter)
            readLimiter.read(36)
            result.put(name, tag)
        }

        return result
    }

    /**
     * Reads the payload of a [Tag], given the name and type.
     *
     * @param type  The type.
     * @param depth The depth.
     * @return The tag.
     * @throws IOException if an I/O error occurs.
     */
    @Throws(IOException::class)
    private fun readTagPayload(type: TagType, depth: Int, readLimiter: NBTReadLimiter): Tag<*> {
        check(depth <= MAX_DEPTH) { "Tried to read NBT tag with too high complexity, depth > 512" }

        when (type) {
            TagType.BYTE -> {
                readLimiter.read(1)
                return ByteTag(`is`.readByte())
            }

            TagType.SHORT -> {
                readLimiter.read(2)
                return ShortTag(`is`.readShort())
            }

            TagType.INT -> {
                readLimiter.read(4)
                return IntTag(`is`.readInt())
            }

            TagType.LONG -> {
                readLimiter.read(8)
                return LongTag(`is`.readLong())
            }

            TagType.FLOAT -> {
                readLimiter.read(4)
                return FloatTag(`is`.readFloat())
            }

            TagType.DOUBLE -> {
                readLimiter.read(8)
                return DoubleTag(`is`.readDouble())
            }

            TagType.BYTE_ARRAY -> {
                readLimiter.read(24)
                val length = `is`.readInt()
                readLimiter.read(length)
                val bytes = ByteArray(length)
                `is`.readFully(bytes)
                return ByteArrayTag(*bytes)
            }

            TagType.STRING -> {
                readLimiter.read(36)
                val s = `is`.readUTF()
                readLimiter.read(2 * s.length)
                return StringTag(s)
            }

            TagType.LIST -> {
                readLimiter.read(37)
                val childType = TagType.byIdOrError(`is`.readUnsignedByte())
                val length = `is`.readInt()
                readLimiter.read(4 * length)

                val tagList: MutableList<Tag<*>> = ArrayList<Tag<*>>(length)
                var i = 0
                while (i < length) {
                    tagList.add(readTagPayload(childType, depth + 1, readLimiter))
                    i++
                }

                return ListTag(childType, tagList)
            }

            TagType.COMPOUND -> {
                readLimiter.read(48)
                return readCompound(depth + 1, readLimiter)
            }

            TagType.INT_ARRAY -> {
                readLimiter.read(37)
                val length = `is`.readInt()
                readLimiter.read(4 * length)
                val ints = IntArray(length)
                var i = 0
                while (i < length) {
                    ints[i] = `is`.readInt()
                    ++i
                }
                return IntArrayTag(*ints)
            }

            else -> throw IOException("Invalid tag type: $type.")
        }
    }

    @Throws(IOException::class)
    override fun close() {
        `is`.close()
    }

    companion object {
        private const val MAX_DEPTH = 512
    }
}
