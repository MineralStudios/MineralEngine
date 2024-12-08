package gg.mineral.server.util.datatypes;

import java.util.UUID;

import com.eatthepath.uuid.FastUUID;

public class UUIDUtil {
    private static final int UUID_INT_ARRAY_LENGTH = 4;

    public static String fromUUID(UUID uuid) {
        return FastUUID.toString(uuid).replace("-", "");
    }

    public static UUID fromString(String string) {
        return FastUUID.parseUUID(string.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
    }

    /**
     * Convert a UUID to its Int-Array representation.
     *
     * @param uuid a UUID
     * @return the Int-Array representation of the UUID
     */
    public static int[] toIntArray(UUID uuid) {
        return new int[] {
                (int) (uuid.getMostSignificantBits() >> 32),
                (int) uuid.getMostSignificantBits(),
                (int) (uuid.getLeastSignificantBits() >> 32),
                (int) uuid.getLeastSignificantBits()
        };
    }

    /**
     * Parses a UUID from an Int-Array representation.
     *
     * @param arr the int array containint the representation
     * @return a UUID
     */
    public static UUID fromIntArray(int[] arr) {
        if (arr.length != UUID_INT_ARRAY_LENGTH)
            return null;

        long mostSigBits = (long) arr[0] << 32 | arr[1] & 0xFFFFFFFFL,
                leastSigBits = (long) arr[2] << 32 | arr[2] & 0xFFFFFFFFL;
        return new UUID(mostSigBits, leastSigBits);
    }

    public static UUID fromName(String name) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes());
    }
}
