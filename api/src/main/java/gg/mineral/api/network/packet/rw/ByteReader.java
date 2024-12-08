package gg.mineral.api.network.packet.rw;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import gg.mineral.api.entity.meta.EntityMetadata;
import gg.mineral.api.entity.meta.EntityMetadataIndex;
import gg.mineral.api.entity.meta.EntityMetadataType;
import gg.mineral.api.inventory.item.ItemStack;
import gg.mineral.api.math.EulerAngle;
import gg.mineral.api.math.Vector;
import gg.mineral.api.nbt.CompoundTag;
import gg.mineral.api.nbt.NBTInputStream;
import gg.mineral.api.nbt.NBTReadLimiter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.handler.codec.DecoderException;
import lombok.val;

public interface ByteReader extends ByteRW {
    default int readVarInt(ByteBuf buf) {
        int value = 0, position = 0;

        byte currentByte;

        do {
            currentByte = buf.readByte();
            value |= (currentByte & 127) << position++ * 7;
            if (position > 5)
                throw new RuntimeException("VarInt too big");
        } while ((currentByte & 128) == 128);

        return value;
    }

    default String readString(ByteBuf buf) {
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
    default UUID readUuid(ByteBuf buf) {
        long msb = buf.readLong(), lsb = buf.readLong();
        return new UUID(msb, lsb);
    }

    default int[] readIntArray(ByteBuf buf, int length) {
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
    default CompoundTag readCompound(ByteBuf buf) {
        return readCompound(buf, false);
    }

    default CompoundTag readCompound(ByteBuf buf, boolean network) {
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
     * Read an item stack from the buffer.
     *
     * @param buf The buffer.
     * @return The stack read, or null.
     */
    default ItemStack readSlot(ByteBuf buf) {
        return readSlot(buf, false);
    }

    /**
     * Read an item stack from the buffer.
     *
     * @param buf     The buffer.
     * @param network Mark network source.
     * @return The stack read, or null.
     */
    default ItemStack readSlot(ByteBuf buf, boolean network) {
        short type = buf.readShort();
        if (type == -1)
            return null;

        short amount = buf.readUnsignedByte(), durability = buf.readShort();

        val tag = readCompound(buf, network);
        val stack = new ItemStack(type, amount, durability);
        stack.readNbt(tag);
        return stack;
    }

    /**
     * Read a list of mob metadata entries from the buffer.
     *
     * @param buf The buffer.
     * @return The metadata.
     */
    default List<EntityMetadata.Entry> readMetadata(ByteBuf buf) {
        val entries = new ArrayList<EntityMetadata.Entry>();
        byte item;
        while ((item = buf.readByte()) != 0x7F) {
            val type = EntityMetadataType.byId(item >> 5);
            int id = item & 0x1f;
            val index = EntityMetadataIndex.getIndex(id, type);

            switch (type) {
                case BYTE ->
                    entries.add(new EntityMetadata.Entry(index, buf.readByte()));
                case SHORT ->
                    entries.add(new EntityMetadata.Entry(index, buf.readShort()));
                case INT ->
                    entries.add(new EntityMetadata.Entry(index, buf.readInt()));
                case FLOAT ->
                    entries.add(new EntityMetadata.Entry(index, buf.readFloat()));
                case STRING ->
                    entries.add(new EntityMetadata.Entry(index, readString(buf)));
                case ITEM ->
                    entries.add(new EntityMetadata.Entry(index, readSlot(buf)));
                case VECTOR -> {
                    int x = buf.readInt(), y = buf.readInt(), z = buf.readInt();
                    entries.add(new EntityMetadata.Entry(index, new Vector(x, y, z)));
                }
                case EULER_ANGLE -> {
                    double x = Math.toRadians(buf.readFloat()), y = Math.toRadians(buf.readFloat()),
                            z = Math.toRadians(buf.readFloat());
                    entries.add(new EntityMetadata.Entry(index, new EulerAngle(x, y, z)));
                    break;
                }
            }
        }
        return entries;
    }
}
