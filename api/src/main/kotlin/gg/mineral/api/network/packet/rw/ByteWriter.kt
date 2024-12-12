package gg.mineral.api.network.packet.rw

import gg.mineral.api.entity.attribute.Attribute
import gg.mineral.api.entity.meta.EntityMetadata
import gg.mineral.api.entity.meta.EntityMetadataType
import gg.mineral.api.inventory.item.ItemStack
import gg.mineral.api.math.EulerAngle
import gg.mineral.api.math.Vector
import gg.mineral.api.nbt.CompoundTag
import gg.mineral.api.nbt.NBTOutputStream
import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.handler.codec.EncoderException
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*

interface ByteWriter : ByteRW {
    fun ByteBuf.writeInt(vararg values: Int) {
        for (value in values) this.writeInt(value)
    }

    fun ByteBuf.writeByte(vararg values: Byte) {
        for (value in values) this.writeByte(value.toInt())
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    fun ByteBuf.writeByte(vararg values: UByte) {
        for (value in values) this.writeByte(value)
    }

    fun ByteBuf.writeFloat(vararg values: Float) {
        for (value in values) this.writeFloat(value)
    }

    fun ByteBuf.writeShort(vararg values: Short) {
        for (value in values) this.writeShort(value)
    }

    fun ByteBuf.writeShort(vararg values: Int) {
        for (value in values) this.writeShort(value)
    }


    /**
     * Writes a Minecraft-style VarInt to the specified `buf`.
     *
     * @param buf   the buffer to read from
     * @param value the integer to write
     */
    fun ByteBuf.writeVarInt(value: Int) {
        // Peel the one and two byte count cases explicitly as they are the most common
        // VarInt sizes
        // that the proxy will write, to improve inlining.
        if ((value and (-0x1 shl 7)) == 0) this.writeByte(value)
        else if ((value and (-0x1 shl 14)) == 0) {
            val w = (value and 0x7F or 0x80) shl 8 or (value ushr 7)
            this.writeShort(w)
        } else this.writeVarIntFull(value)
    }

    fun ByteBuf.writeVarIntFull(value: Int) {
        // See https://steinborn.me/posts/performance/how-fast-can-you-write-a-varint/
        if ((value and (-0x1 shl 7)) == 0) this.writeByte(value)
        else if ((value and (-0x1 shl 14)) == 0) {
            val w = (value and 0x7F or 0x80) shl 8 or (value ushr 7)
            this.writeShort(w)
        } else if ((value and (-0x1 shl 21)) == 0) {
            val w = (value and 0x7F or 0x80) shl 16 or (((value ushr 7) and 0x7F or 0x80) shl 8) or (value ushr 14)
            this.writeMedium(w)
        } else if ((value and (-0x1 shl 28)) == 0) {
            val w = ((value and 0x7F or 0x80) shl 24 or (((value ushr 7) and 0x7F or 0x80) shl 16)
                    or (((value ushr 14) and 0x7F or 0x80) shl 8) or (value ushr 21))
            this.writeInt(w)
        } else {
            val w = ((value and 0x7F or 0x80) shl 24 or (((value ushr 7) and 0x7F or 0x80) shl 16
                    ) or (((value ushr 14) and 0x7F or 0x80) shl 8) or ((value ushr 21) and 0x7F or 0x80))
            this.writeInt(w)
            this.writeByte(value ushr 28)
        }
    }

    fun ByteBuf.writeString(vararg strings: String) {
        for (string in strings) this.writeString(string)
    }

    fun ByteBuf.writeString(string: String) {
        val stringBytes = string.toByteArray(StandardCharsets.UTF_8)

        if (stringBytes.size > 32767) throw EncoderException("String too big (was " + string.length + " bytes encoded, max " + 32767 + ")")

        this.writeVarInt(stringBytes.size)
        this.writeBytes(stringBytes)
    }

    fun ByteBuf.writeString(strings: Collection<String>) {
        for (string in strings) this.writeString(string)
    }

    fun ByteBuf.writeUuid(uuid: UUID) {
        this.writeLong(uuid.mostSignificantBits)
        this.writeLong(uuid.leastSignificantBits)
    }

    fun ByteBuf.writeIntArray(ints: IntArray) {
        for (i in ints.indices) this.writeInt(ints[i])
    }

    /**
     * Write an uncompressed compound NBT tag to the buffer.
     * NBTOutputStream
     *
     * @param buf  The buffer.
     * @param data The tag to write, or null.
     */
    fun ByteBuf.writeCompound(data: CompoundTag?) {
        if (data == null) {
            this.writeShort(-1)
            return
        }

        val out = ByteArrayOutputStream()
        try {
            NBTOutputStream(out).use { str ->
                str.writeTag(data)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return
        }

        this.writeBytes(out.toByteArray())
    }

    /**
     * Write an item stack to the buffer.
     *
     * @param buf   The buffer.
     * @param stack The stack to write, or null.
     */
    fun ByteBuf.writeSlot(stack: ItemStack?) {
        if (stack == null || stack.typeId.toInt() == 0) {
            this.writeShort(-1)
            return
        }

        this.writeShort(stack.typeId.toInt())
        this.writeByte(stack.amount.toInt())
        this.writeShort(stack.durability.toInt())
        val result = CompoundTag()
        stack.writeNbt(result)
        this.writeCompound(if (result.isEmpty) null else result)
    }

    /**
     * Write a list of mob metadata entries to the buffer.
     *
     * @param buf     The buffer.
     * @param entries The metadata.
     */
    fun ByteBuf.writeMetadata(entries: List<EntityMetadata.Entry>) {
        for (entry in entries) {
            val index = entry.index
            val value = entry.value

            val type = index.type.id.toInt()
            val id = index.index
            this.writeByte((type shl 5) or id)

            when (index.type) {
                EntityMetadataType.BYTE -> this.writeByte((value as Byte).toInt())
                EntityMetadataType.SHORT -> this.writeShort((value as Short).toInt())
                EntityMetadataType.INT -> this.writeInt(value as Int)
                EntityMetadataType.FLOAT -> this.writeFloat(value as Float)
                EntityMetadataType.STRING -> this.writeString(value as String)
                EntityMetadataType.ITEM -> this.writeSlot(value as ItemStack)
                EntityMetadataType.VECTOR -> {
                    val vector = value as Vector
                    this.writeInt(vector.blockX, vector.blockY, vector.blockZ)
                }

                EntityMetadataType.EULER_ANGLE -> {
                    val angle = value as EulerAngle
                    this.writeFloat(
                        Math.toDegrees(angle.x).toFloat(),
                        Math.toDegrees(angle.y).toFloat(),
                        Math.toDegrees(angle.z).toFloat()
                    )
                }
            }
        }

        this.writeByte(127)
    }

    fun ByteBuf.writeProperties(props: Map<String, Attribute.Property>) {
        for ((key, value) in props) {
            this.writeString(key)
            this.writeDouble(value.value)

            val modifiers = value.modifiers
            if (modifiers.isEmpty()) {
                this.writeShort(0)
                continue
            }

            this.writeShort(modifiers.size)
            for (modifier in modifiers) {
                this.writeUuid(modifier.id)
                this.writeDouble(modifier.amount.toDouble())
                this.writeByte(modifier.operation.id)
            }
        }
    }

    fun serialize(packet: Packet.OUTGOING): ByteBuf {
        val data = Unpooled.buffer()
        data.writeVarInt(packet.id.toInt())
        packet.serialize(data)

        val length = data.writerIndex()
        val lengthSize = getVarIntSize(length)
        val os = Unpooled.buffer(lengthSize + length)
        os.writeVarInt(length)
        os.writeBytes(data)


        return os
    }
}
