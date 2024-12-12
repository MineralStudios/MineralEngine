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
import java.nio.charset.StandardCharsets
import java.util.*

interface ByteReader : ByteRW {
    fun ByteBuf.readVarInt(): Int {
        var value = 0
        var position = 0

        var currentByte: Byte

        do {
            currentByte = this.readByte()
            value = value or ((currentByte.toInt() and 127) shl position++ * 7)
            if (position > 5) throw RuntimeException("VarInt too big")
        } while ((currentByte.toInt() and 128) == 128)

        return value
    }

    fun ByteBuf.readString(): String {
        val length = this.readVarInt()

        if (length < 0) throw DecoderException("The received encoded string buffer length is less than zero! Weird string!")

        val array = ByteArray(length)
        this.readBytes(array)

        return String(array, StandardCharsets.UTF_8)
    }

    /**
     * Reads a UUID from the `buf`.
     *
     * @return the UUID from the buffer
     */
    fun ByteBuf.readUuid(): UUID {
        val msb = this.readLong()
        val lsb = this.readLong()
        return UUID(msb, lsb)
    }

    fun ByteBuf.readIntArray(length: Int): IntArray {
        val ints = IntArray(length)

        for (i in ints.indices) ints[i] = this.readInt()

        return ints
    }

    /**
     * Read an uncompressed compound NBT tag from the buffer.
     *
     * @param buf The buffer.
     * @return The tag read, or null.
     */
    fun ByteBuf.readCompound(): CompoundTag? {
        return this.readCompound(false)
    }

    fun ByteBuf.readCompound(network: Boolean): CompoundTag? {
        val idx = this.readerIndex()
        if (this.readByte().toInt() == 0) return null

        this.readerIndex(idx)
        try {
            NBTInputStream(ByteBufInputStream(this), false).use { str ->
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
    fun ByteBuf.readSlot(): ItemStack? {
        return readSlot(false)
    }

    /**
     * Read an item stack from the buffer.
     *
     * @param buf     The buffer.
     * @param network Mark network source.
     * @return The stack read, or null.
     */
    fun ByteBuf.readSlot(network: Boolean): ItemStack? {
        val type = this.readShort()
        if (type.toInt() == -1) return null

        val amount = this.readUnsignedByte().toUByte()
        val durability = this.readShort()

        val tag = this.readCompound(network)
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
    fun ByteBuf.readMetadata(): List<EntityMetadata.Entry> {
        val entries = ArrayList<EntityMetadata.Entry>()
        var item: Byte
        while ((this.readByte().also { item = it }).toInt() != 0x7F) {
            val type = byId(item.toInt() shr 5)
            val id = item.toInt() and 0x1f
            val index = getIndex(id, type)

            when (type) {
                EntityMetadataType.BYTE -> entries.add(
                    EntityMetadata.Entry(
                        index!!, this.readByte()
                    )
                )

                EntityMetadataType.SHORT -> entries.add(
                    EntityMetadata.Entry(
                        index!!, this.readShort()
                    )
                )

                EntityMetadataType.INT -> entries.add(
                    EntityMetadata.Entry(
                        index!!, this.readInt()
                    )
                )

                EntityMetadataType.FLOAT -> entries.add(
                    EntityMetadata.Entry(
                        index!!, this.readFloat()
                    )
                )

                EntityMetadataType.STRING -> entries.add(
                    EntityMetadata.Entry(
                        index!!, this.readString()
                    )
                )

                EntityMetadataType.ITEM -> entries.add(
                    EntityMetadata.Entry(
                        index!!, this.readSlot()!!
                    )
                )

                EntityMetadataType.VECTOR -> {
                    val x = this.readInt()
                    val y = this.readInt()
                    val z = this.readInt()
                    entries.add(EntityMetadata.Entry(index!!, Vector(x, y, z)))
                }

                EntityMetadataType.EULER_ANGLE -> {
                    val x = Math.toRadians(this.readFloat().toDouble())
                    val y = Math.toRadians(this.readFloat().toDouble())
                    val z = Math.toRadians(this.readFloat().toDouble())
                    entries.add(EntityMetadata.Entry(index!!, EulerAngle(x, y, z)))
                    break
                }
            }
        }
        return entries
    }
}
