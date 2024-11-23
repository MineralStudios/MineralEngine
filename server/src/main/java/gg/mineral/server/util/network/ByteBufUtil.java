package gg.mineral.server.util.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import gg.mineral.server.entity.attribute.Property;
import gg.mineral.server.entity.metadata.EntityMetadata;
import gg.mineral.server.entity.metadata.EntityMetadataIndex;
import gg.mineral.server.entity.metadata.EntityMetadataType;
import gg.mineral.server.inventory.item.ItemStack;
import gg.mineral.server.util.math.EulerAngle;
import gg.mineral.server.util.math.Vector;
import gg.mineral.server.util.nbt.CompoundTag;
import gg.mineral.server.util.nbt.NBTInputStream;
import gg.mineral.server.util.nbt.NBTOutputStream;
import gg.mineral.server.util.nbt.NBTReadLimiter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import lombok.val;

public class ByteBufUtil {
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    public static int readVarInt(ByteBuf buf) {
        int value = 0;
        int position = 0;

        byte currentByte;

        do {
            currentByte = buf.readByte();
            value |= (currentByte & 127) << position++ * 7;
            if (position > 5) {
                throw new RuntimeException("VarInt too big");
            }
        } while ((currentByte & 128) == 128);

        return value;
    }

    public static int readVarInt(ByteBuffer buf) {
        int value = 0;
        int position = 0;

        byte currentByte;

        do {
            currentByte = buf.get();
            value |= (currentByte & 127) << position++ * 7;
            if (position > 5) {
                throw new RuntimeException("VarInt too big");
            }
        } while ((currentByte & 128) == 128);

        return value;
    }

    public static int getVarIntSize(int value) {
        for (int position = 1; position < 5; ++position)
            if ((value & -1 << position * 7) == 0)
                return position;

        return 5;
    }

    /**
     * Writes a Minecraft-style VarInt to the specified {@code buf}.
     * 
     * @param buf   the buffer to read from
     * @param value the integer to write
     */
    public static void writeVarInt(ByteBuf buf, int value) {
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

    private static void writeVarIntFull(ByteBuf buf, int value) {
        // See https://steinborn.me/posts/performance/how-fast-can-you-write-a-varint/
        if ((value & (0xFFFFFFFF << 7)) == 0) {
            buf.writeByte(value);
        } else if ((value & (0xFFFFFFFF << 14)) == 0) {
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

    public static void writeString(ByteBuf buf, String... strings) {
        for (val string : strings)
            writeString(buf, string);
    }

    public static void writeString(ByteBuf buf, String string) {
        val stringBytes = string.getBytes(UTF_8);

        if (stringBytes.length > 32767)
            throw new EncoderException("String too big (was " + string.length() + " bytes encoded, max " + 32767 + ")");

        writeVarInt(buf, stringBytes.length);
        buf.writeBytes(stringBytes);
    }

    public static String readString(ByteBuf buf) {
        int length = readVarInt(buf);

        if (length < 0)
            throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");

        val array = new byte[length];
        buf.readBytes(array);

        return new String(array, UTF_8);
    }

    /**
     * Reads an UUID from the {@code buf}.
     * 
     * @param buf the buffer to read from
     * @return the UUID from the buffer
     */
    public static UUID readUuid(ByteBuf buf) {
        long msb = buf.readLong();
        long lsb = buf.readLong();
        return new UUID(msb, lsb);
    }

    public static void writeUuid(ByteBuf buf, UUID uuid) {
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
    }

    public static void writeIntArray(ByteBuf buf, int[] ints) {
        for (int i = 0; i < ints.length; i++)
            buf.writeInt(ints[i]);
    }

    public static int[] readIntArray(ByteBuf buf, int length) {
        val ints = new int[length];

        for (int i = 0; i < ints.length; i++)
            ints[i] = buf.readInt();

        return ints;
    }

    /**
     * Read an uncompressed compound NBT tag from the buffer.
     *
     * @param buf The buffer.
     * @return The tag read, or null.
     */
    public static CompoundTag readCompound(ByteBuf buf) {
        return readCompound(buf, false);
    }

    private static CompoundTag readCompound(ByteBuf buf, boolean network) {
        int idx = buf.readerIndex();
        if (buf.readByte() == 0)
            return null;

        buf.readerIndex(idx);
        try (val str = new NBTInputStream(new ByteBufInputStream(buf), false)) {
            return str.readCompound(
                    network ? new NBTReadLimiter(2097152L) : NBTReadLimiter.UNLIMITED);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Write an uncompressed compound NBT tag to the buffer.
     * NBTOutputStream
     * 
     * @param buf  The buffer.
     * @param data The tag to write, or null.
     */
    public static void writeCompound(ByteBuf buf, CompoundTag data) {
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
     * Read an item stack from the buffer.
     *
     * @param buf The buffer.
     * @return The stack read, or null.
     */
    public static ItemStack readSlot(ByteBuf buf) {
        return readSlot(buf, false);
    }

    /**
     * Read an item stack from the buffer.
     *
     * @param buf     The buffer.
     * @param network Mark network source.
     * @return The stack read, or null.
     */
    public static ItemStack readSlot(ByteBuf buf, boolean network) {
        short type = buf.readShort();
        if (type == -1)
            return null;

        short amount = buf.readUnsignedByte();
        short durability = buf.readShort();

        val tag = readCompound(buf, network);
        val stack = new ItemStack(type, amount, durability);
        stack.readNbt(tag);
        return stack;
    }

    /**
     * Write an item stack to the buffer.
     *
     * @param buf   The buffer.
     * @param stack The stack to write, or null.
     */
    public static void writeSlot(ByteBuf buf, ItemStack stack) {
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
     * Read a list of mob metadata entries from the buffer.
     *
     * @param buf The buffer.
     * @return The metadata.
     */
    public static List<EntityMetadata.Entry> readMetadata(ByteBuf buf) {
        val entries = new ArrayList<EntityMetadata.Entry>();
        byte item;
        while ((item = buf.readByte()) != 0x7F) {
            val type = EntityMetadataType.byId(item >> 5);
            int id = item & 0x1f;
            val index = EntityMetadataIndex.getIndex(id, type);

            switch (type) {
                case BYTE:
                    entries.add(new EntityMetadata.Entry(index, buf.readByte()));
                    break;
                case SHORT:
                    entries.add(new EntityMetadata.Entry(index, buf.readShort()));
                    break;
                case INT:
                    entries.add(new EntityMetadata.Entry(index, buf.readInt()));
                    break;
                case FLOAT:
                    entries.add(new EntityMetadata.Entry(index, buf.readFloat()));
                    break;
                case STRING:
                    entries.add(new EntityMetadata.Entry(index, readString(buf)));
                    break;
                case ITEM:
                    entries.add(new EntityMetadata.Entry(index, readSlot(buf)));
                    break;
                case VECTOR: {
                    int x = buf.readInt();
                    int y = buf.readInt();
                    int z = buf.readInt();
                    entries.add(new EntityMetadata.Entry(index, new Vector(x, y, z)));
                    break;
                }
                case EULER_ANGLE: {
                    double x = Math.toRadians(buf.readFloat());
                    double y = Math.toRadians(buf.readFloat());
                    double z = Math.toRadians(buf.readFloat());
                    entries.add(new EntityMetadata.Entry(index, new EulerAngle(x, y, z)));
                    break;
                }
            }
        }
        return entries;
    }

    /**
     * Write a list of mob metadata entries to the buffer.
     *
     * @param buf     The buffer.
     * @param entries The metadata.
     */
    public static void writeMetadata(ByteBuf buf, List<EntityMetadata.Entry> entries) {
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

    public static void writeProperties(ByteBuf buf, Map<String, Property> props) {
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

}
