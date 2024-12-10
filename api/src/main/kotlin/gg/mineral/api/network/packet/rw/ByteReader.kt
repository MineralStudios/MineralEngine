package gg.mineral.api.network.packet.rw

import gg.mineral.api.entity.meta.EntityMetadata
import gg.mineral.api.entity.meta.EntityMetadataIndex.Companion.getIndex
import gg.mineral.api.entity.meta.EntityMetadataType
import gg.mineral.api.entity.meta.EntityMetadataType.Companion.byId
import gg.mineral.api.inventory.item.ItemStack
import gg.mineral.api.math.EulerAngle
import gg.mineral.api.math.Vector
import gg.mineral.api.nbt.CompoundTag
import gg.mineral.api.nbt.NBTInputStream
import gg.mineral.api.nbt.NBTReadLimiter
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufInputStream
import io.netty.handler.codec.DecoderException
import java.io.IOException
import java.util.*

interface ByteReader : ByteRW {
    fun readVarInt(buf: ByteBuf): Int {
        var value = 0
        var position = 0

        var currentByte: Byte

        do {
            currentByte = buf.readByte()
            value = value or ((currentByte.toInt() and 127) shl position++ * 7)
            if (position > 5) throw RuntimeException("VarInt too big")
        } while ((currentByte.toInt() and 128) == 128)

        return value
    }

    fun readString(buf: ByteBuf): String {
        val length = readVarInt(buf)

        if (length < 0) throw DecoderException("The received encoded string buffer length is less than zero! Weird string!")

        val array = ByteArray(length)
        buf.readBytes(array)

        return String(array, ByteRW.UTF_8)
    }

    /**
     * Reads a UUID from the `buf`.
     *
     * @param buf the buffer to read from
     * @return the UUID from the buffer
     */
    fun readUuid(buf: ByteBuf): UUID {
        val msb = buf.readLong()
        val lsb = buf.readLong()
        return UUID(msb, lsb)
    }

    fun readIntArray(buf: ByteBuf, length: Int): IntArray {
        val ints = IntArray(length)

        for (i in ints.indices) ints[i] = buf.readInt()

        return ints
    }

    /**
     * Read an uncompressed compound NBT tag from the buffer.
     *
     * @param buf The buffer.
     * @return The tag read, or null.
     */
    fun readCompound(buf: ByteBuf): CompoundTag? {
        return readCompound(buf, false)
    }

    fun readCompound(buf: ByteBuf, network: Boolean): CompoundTag? {
        val idx = buf.readerIndex()
        if (buf.readByte().toInt() == 0) return null

        buf.readerIndex(idx)
        try {
            NBTInputStream(ByteBufInputStream(buf), false).use { str ->
                return str.readCompound(
                    if (network) NBTReadLimiter(2097152L) else NBTReadLimiter.UNLIMITED
                )
            }
        } catch (e: IOException) {
            return null
        }
    }

    /**
     * Read an item stack from the buffer.
     *
     * @param buf The buffer.
     * @return The stack read, or null.
     */
    fun readSlot(buf: ByteBuf): ItemStack? {
        return readSlot(buf, false)
    }

    /**
     * Read an item stack from the buffer.
     *
     * @param buf     The buffer.
     * @param network Mark network source.
     * @return The stack read, or null.
     */
    fun readSlot(buf: ByteBuf, network: Boolean): ItemStack? {
        val type = buf.readShort()
        if (type.toInt() == -1) return null

        val amount = buf.readUnsignedByte()
        val durability = buf.readShort()

        val tag = readCompound(buf, network)
        val stack = ItemStack(type, amount, durability)
        stack.readNbt(tag)
        return stack
    }

    /**
     * Read a list of mob metadata entries from the buffer.
     *
     * @param buf The buffer.
     * @return The metadata.
     */
    fun readMetadata(buf: ByteBuf): List<EntityMetadata.Entry> {
        val entries = ArrayList<EntityMetadata.Entry>()
        var item: Byte
        while ((buf.readByte().also { item = it }).toInt() != 0x7F) {
            val type = byId(item.toInt() shr 5)
            val id = item.toInt() and 0x1f
            val index = getIndex(id, type)

            when (type) {
                EntityMetadataType.BYTE -> entries.add(
                    EntityMetadata.Entry(
                        index!!, buf.readByte()
                    )
                )

                EntityMetadataType.SHORT -> entries.add(
                    EntityMetadata.Entry(
                        index!!, buf.readShort()
                    )
                )

                EntityMetadataType.INT -> entries.add(
                    EntityMetadata.Entry(
                        index!!, buf.readInt()
                    )
                )

                EntityMetadataType.FLOAT -> entries.add(
                    EntityMetadata.Entry(
                        index!!, buf.readFloat()
                    )
                )

                EntityMetadataType.STRING -> entries.add(
                    EntityMetadata.Entry(
                        index!!, readString(buf)
                    )
                )

                EntityMetadataType.ITEM -> entries.add(
                    EntityMetadata.Entry(
                        index!!, readSlot(buf)!!
                    )
                )

                EntityMetadataType.VECTOR -> {
                    val x = buf.readInt()
                    val y = buf.readInt()
                    val z = buf.readInt()
                    entries.add(EntityMetadata.Entry(index!!, Vector(x, y, z)))
                }

                EntityMetadataType.EULER_ANGLE -> {
                    val x = Math.toRadians(buf.readFloat().toDouble())
                    val y = Math.toRadians(buf.readFloat().toDouble())
                    val z = Math.toRadians(buf.readFloat().toDouble())
                    entries.add(EntityMetadata.Entry(index!!, EulerAngle(x, y, z)))
                    break
                }
            }
        }
        return entries
    }
}
