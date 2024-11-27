package gg.mineral.api.network.packet.rw;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import gg.mineral.api.entity.attribute.Attribute;
import gg.mineral.api.entity.meta.EntityMetadata;
import gg.mineral.api.inventory.item.ItemStack;
import gg.mineral.api.math.EulerAngle;
import gg.mineral.api.math.Vector;
import gg.mineral.api.nbt.CompoundTag;
import gg.mineral.api.nbt.NBTOutputStream;
import gg.mineral.api.network.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.EncoderException;
import lombok.val;

public interface ByteWriter extends ByteRW {
    /**
     * Writes a Minecraft-style VarInt to the specified {@code buf}.
     * 
     * @param buf   the buffer to read from
     * @param value the integer to write
     */
    default void writeVarInt(ByteBuf buf, int value) {
        // Peel the one and two byte count cases explicitly as they are the most common
        // VarInt sizes
        // that the proxy will write, to improve inlining.
        if ((value & (0xFFFFFFFF << 7)) == 0)
            buf.writeByte(value);
        else if ((value & (0xFFFFFFFF << 14)) == 0) {
            int w = (value & 0x7F | 0x80) << 8 | (value >>> 7);
            buf.writeShort(w);
        } else
            writeVarIntFull(buf, value);

    }

    default void writeVarIntFull(ByteBuf buf, int value) {
        // See https://steinborn.me/posts/performance/how-fast-can-you-write-a-varint/
        if ((value & (0xFFFFFFFF << 7)) == 0)
            buf.writeByte(value);
        else if ((value & (0xFFFFFFFF << 14)) == 0) {
            int w = (value & 0x7F | 0x80) << 8 | (value >>> 7);
            buf.writeShort(w);
        } else if ((value & (0xFFFFFFFF << 21)) == 0) {
            int w = (value & 0x7F | 0x80) << 16 | ((value >>> 7) & 0x7F | 0x80) << 8 | (value >>> 14);
            buf.writeMedium(w);
        } else if ((value & (0xFFFFFFFF << 28)) == 0) {
            int w = (value & 0x7F | 0x80) << 24 | (((value >>> 7) & 0x7F | 0x80) << 16)
                    | ((value >>> 14) & 0x7F | 0x80) << 8 | (value >>> 21);
            buf.writeInt(w);
        } else {
            int w = (value & 0x7F | 0x80) << 24 | ((value >>> 7) & 0x7F | 0x80) << 16
                    | ((value >>> 14) & 0x7F | 0x80) << 8 | ((value >>> 21) & 0x7F | 0x80);
            buf.writeInt(w);
            buf.writeByte(value >>> 28);
        }
    }

    default void writeString(ByteBuf buf, String... strings) {
        for (val string : strings)
            writeString(buf, string);
    }

    default void writeString(ByteBuf buf, String string) {
        val stringBytes = string.getBytes(UTF_8);

        if (stringBytes.length > 32767)
            throw new EncoderException("String too big (was " + string.length() + " bytes encoded, max " + 32767 + ")");

        writeVarInt(buf, stringBytes.length);
        buf.writeBytes(stringBytes);
    }

    default void writeString(ByteBuf buf, Collection<String> strings) {
        for (val string : strings)
            writeString(buf, string);
    }

    default void writeUuid(ByteBuf buf, UUID uuid) {
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
    }

    default void writeIntArray(ByteBuf buf, int[] ints) {
        for (int i = 0; i < ints.length; i++)
            buf.writeInt(ints[i]);
    }

    /**
     * Write an uncompressed compound NBT tag to the buffer.
     * NBTOutputStream
     * 
     * @param buf  The buffer.
     * @param data The tag to write, or null.
     */
    default void writeCompound(ByteBuf buf, CompoundTag data) {
        if (data == null) {
            buf.writeShort(-1);
            return;
        }

        val out = new ByteArrayOutputStream();
        try (val str = new NBTOutputStream(out)) {
            str.writeTag(data);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        buf.writeBytes(out.toByteArray());
    }

    /**
     * Write an item stack to the buffer.
     *
     * @param buf   The buffer.
     * @param stack The stack to write, or null.
     */
    default void writeSlot(ByteBuf buf, ItemStack stack) {
        if (stack == null || stack.getTypeId() == 0) {
            buf.writeShort(-1);
            return;
        }

        buf.writeShort(stack.getTypeId());
        buf.writeByte(stack.getAmount());
        buf.writeShort(stack.getDurability());
        val result = new CompoundTag();
        stack.writeNbt(result);
        writeCompound(buf, result.isEmpty() ? null : result);
    }

    /**
     * Write a list of mob metadata entries to the buffer.
     *
     * @param buf     The buffer.
     * @param entries The metadata.
     */
    default void writeMetadata(ByteBuf buf, List<EntityMetadata.Entry> entries) {
        for (val entry : entries) {
            val index = entry.index;
            val value = entry.value;

            if (value == null)
                continue;

            int type = index.getType().getId();
            int id = index.getIndex();
            buf.writeByte((type << 5) | id);

            switch (index.getType()) {
                case BYTE:
                    buf.writeByte((Byte) value);
                    break;
                case SHORT:
                    buf.writeShort((Short) value);
                    break;
                case INT:
                    buf.writeInt((Integer) value);
                    break;
                case FLOAT:
                    buf.writeFloat((Float) value);
                    break;
                case STRING:
                    writeString(buf, (String) value);
                    break;
                case ITEM:
                    writeSlot(buf, (ItemStack) value);
                    break;
                case VECTOR: {
                    Vector vector = (Vector) value;
                    buf.writeInt(vector.getBlockX());
                    buf.writeInt(vector.getBlockY());
                    buf.writeInt(vector.getBlockZ());
                    break;
                }
                case EULER_ANGLE: {
                    EulerAngle angle = (EulerAngle) value;
                    buf.writeFloat((float) Math.toDegrees(angle.getX()));
                    buf.writeFloat((float) Math.toDegrees(angle.getY()));
                    buf.writeFloat((float) Math.toDegrees(angle.getZ()));
                    break;
                }
            }
        }

        buf.writeByte(127);
    }

    default void writeProperties(ByteBuf buf, Map<String, Attribute.Property> props) {
        for (val property : props.entrySet()) {
            writeString(buf, property.getKey());
            buf.writeDouble(property.getValue().value());

            val modifiers = property.getValue().modifiers();
            if (modifiers == null || modifiers.isEmpty()) {
                buf.writeShort(0);
                continue;
            }

            buf.writeShort(modifiers.size());
            for (val modifier : modifiers) {
                writeUuid(buf, modifier.getId());
                buf.writeDouble(modifier.getAmount());
                buf.writeByte(modifier.getOperation().getId());
            }
        }
    }

    default ByteBuf serialize(Packet.OUTGOING packet) {
        val data = Unpooled.buffer();
        writeVarInt(data, packet.getId());
        packet.serialize(data);
        int length = data.writerIndex();
        int lengthSize = getVarIntSize(length);
        val os = Unpooled.buffer(lengthSize + length);
        writeVarInt(os, length);
        os.writeBytes(data);
        return os;
    }
}
