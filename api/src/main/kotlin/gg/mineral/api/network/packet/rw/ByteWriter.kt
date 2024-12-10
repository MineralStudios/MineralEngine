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
import java.util.*

interface ByteWriter : ByteRW {
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

    fun writeString(buf: ByteBuf, vararg strings: String) {
        for (string in strings) writeString(buf, string)
    }

    fun writeString(buf: ByteBuf, string: String) {
        val stringBytes = string.toByteArray(ByteRW.UTF_8)

        if (stringBytes.size > 32767) throw EncoderException("String too big (was " + string.length + " bytes encoded, max " + 32767 + ")")

        buf.writeVarInt(stringBytes.size)
        buf.writeBytes(stringBytes)
    }

    fun writeString(buf: ByteBuf, strings: Collection<String>) {
        for (string in strings) writeString(buf, string)
    }

    fun writeUuid(buf: ByteBuf, uuid: UUID) {
        buf.writeLong(uuid.mostSignificantBits)
        buf.writeLong(uuid.leastSignificantBits)
    }

    fun writeIntArray(buf: ByteBuf, ints: IntArray) {
        for (i in ints.indices) buf.writeInt(ints[i])
    }

    /**
     * Write an uncompressed compound NBT tag to the buffer.
     * NBTOutputStream
     *
     * @param buf  The buffer.
     * @param data The tag to write, or null.
     */
    fun writeCompound(buf: ByteBuf, data: CompoundTag?) {
        if (data == null) {
            buf.writeShort(-1)
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

        buf.writeBytes(out.toByteArray())
    }

    /**
     * Write an item stack to the buffer.
     *
     * @param buf   The buffer.
     * @param stack The stack to write, or null.
     */
    fun writeSlot(buf: ByteBuf, stack: ItemStack?) {
        if (stack == null || stack.typeId.toInt() == 0) {
            buf.writeShort(-1)
            return
        }

        buf.writeShort(stack.typeId.toInt())
        buf.writeByte(stack.amount.toInt())
        buf.writeShort(stack.durability.toInt())
        val result = CompoundTag()
        stack.writeNbt(result)
        writeCompound(buf, if (result.isEmpty) null else result)
    }

    /**
     * Write a list of mob metadata entries to the buffer.
     *
     * @param buf     The buffer.
     * @param entries The metadata.
     */
    fun writeMetadata(buf: ByteBuf, entries: List<EntityMetadata.Entry>) {
        for (entry in entries) {
            val index = entry.index
            val value = entry.value

            val type = index.type.id.toInt()
            val id = index.index
            buf.writeByte((type shl 5) or id)

            when (index.type) {
                EntityMetadataType.BYTE -> buf.writeByte((value as Byte).toInt())
                EntityMetadataType.SHORT -> buf.writeShort((value as Short).toInt())
                EntityMetadataType.INT -> buf.writeInt(value as Int)
                EntityMetadataType.FLOAT -> buf.writeFloat(value as Float)
                EntityMetadataType.STRING -> writeString(buf, value as String)
                EntityMetadataType.ITEM -> writeSlot(buf, value as ItemStack)
                EntityMetadataType.VECTOR -> {
                    val vector = value as Vector
                    buf.writeInt(vector.blockX)
                    buf.writeInt(vector.blockY)
                    buf.writeInt(vector.blockZ)
                }

                EntityMetadataType.EULER_ANGLE -> {
                    val angle = value as EulerAngle
                    buf.writeFloat(Math.toDegrees(angle.x).toFloat())
                    buf.writeFloat(Math.toDegrees(angle.y).toFloat())
                    buf.writeFloat(Math.toDegrees(angle.z).toFloat())
                }
            }
        }

        buf.writeByte(127)
    }

    fun writeProperties(buf: ByteBuf, props: Map<String, Attribute.Property>) {
        for ((key, value) in props) {
            writeString(buf, key)
            buf.writeDouble(value.value)

            val modifiers = value.modifiers
            if (modifiers.isEmpty()) {
                buf.writeShort(0)
                continue
            }

            buf.writeShort(modifiers.size)
            for (modifier in modifiers) {
                writeUuid(buf, modifier.id)
                buf.writeDouble(modifier.amount.toDouble())
                buf.writeByte(modifier.operation.id)
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
