package gg.mineral.server.util.network;

import java.nio.charset.Charset;
import java.util.UUID;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;

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

    public static int getVarIntSize(int value) {
        for (int position = 1; position < 5; ++position) {
            if ((value & -1 << position * 7) == 0) {
                return position;
            }
        }

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
        if ((value & (0xFFFFFFFF << 7)) == 0) {
            buf.writeByte(value);
        } else if ((value & (0xFFFFFFFF << 14)) == 0) {
            int w = (value & 0x7F | 0x80) << 8 | (value >>> 7);
            buf.writeShort(w);
        } else {
            writeVarIntFull(buf, value);
        }
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

    public static void writeString(ByteBuf buf, String string) {
        byte[] stringBytes = string.getBytes(UTF_8);

        if (stringBytes.length > 32767) {
            throw new EncoderException("String too big (was " + string.length() + " bytes encoded, max " + 32767 + ")");
        }

        writeVarInt(buf, stringBytes.length);
        buf.writeBytes(stringBytes);
    }

    public static String readString(ByteBuf buf) {
        int length = readVarInt(buf);

        if (length < 0) {
            throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
        }

        byte[] array = new byte[length];
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

    public static void writeBytes(ByteBuf buf, byte[] bytes) {
        writeVarInt(buf, bytes.length);
        buf.writeBytes(bytes);
    }

    public static byte[] readBytes(ByteBuf buf) {
        byte[] bytes = new byte[ByteBufUtil.readVarInt(buf)];
        buf.readBytes(bytes);
        return bytes;
    }

    public static void writeIntArray(ByteBuf buf, int[] ints) {
        writeVarInt(buf, ints.length);

        for (int i = 0; i < ints.length; i++) {
            buf.writeInt(ints[i]);
        }
    }

    public static int[] readIntArray(ByteBuf buf) {
        int length = ByteBufUtil.readVarInt(buf);
        int[] ints = new int[length];

        for (int i = 0; i < ints.length; i++) {
            ints[i] = buf.readInt();
        }

        return ints;
    }
}
