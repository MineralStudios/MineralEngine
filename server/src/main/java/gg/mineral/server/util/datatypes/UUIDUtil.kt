package gg.mineral.server.util.datatypes

import com.eatthepath.uuid.FastUUID
import java.util.*

object UUIDUtil {
    private const val UUID_INT_ARRAY_LENGTH = 4

    fun fromUUID(uuid: UUID): String {
        return FastUUID.toString(uuid).replace("-", "")
    }

    fun fromString(string: String): UUID {
        return FastUUID.parseUUID(
            string.replaceFirst(
                "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})".toRegex(),
                "$1-$2-$3-$4-$5"
            )
        )
    }

    /**
     * Convert a UUID to its Int-Array representation.
     *
     * @param uuid a UUID
     * @return the Int-Array representation of the UUID
     */
    fun toIntArray(uuid: UUID): IntArray {
        return intArrayOf(
            (uuid.mostSignificantBits shr 32).toInt(),
            uuid.mostSignificantBits.toInt(),
            (uuid.leastSignificantBits shr 32).toInt(),
            uuid.leastSignificantBits.toInt()
        )
    }

    /**
     * Parses a UUID from an Int-Array representation.
     *
     * @param arr the int array containint the representation
     * @return a UUID
     */
    fun fromIntArray(arr: IntArray): UUID? {
        if (arr.size != UUID_INT_ARRAY_LENGTH) return null

        val mostSigBits = arr[0].toLong() shl 32 or (arr[1].toLong() and 0xFFFFFFFFL)
        val leastSigBits = arr[2].toLong() shl 32 or (arr[2].toLong() and 0xFFFFFFFFL)
        return UUID(mostSigBits, leastSigBits)
    }

    fun fromName(name: String): UUID {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:$name").toByteArray())
    }
}
