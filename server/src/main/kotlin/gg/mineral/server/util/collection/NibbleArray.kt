package gg.mineral.server.util.collection

import java.util.*

/**
 * An array of nibbles (4-bit values) stored efficiently as a byte array of half
 * the size.
 *
 *
 *
 * The even indices are stored in the least significant nibble and the odd
 * indices in the most
 * significant bits. For example, [1 5 8 15] is stored as [0x51 0xf8].
 */
class NibbleArray {
    /**
     * Get the raw bytes of this nibble array. Modifying the returned array will
     * modify the internal
     * representation of this nibble array.
     *
     * @return The raw bytes.
     */
    val rawData: ByteArray

    /**
     * Construct a new NibbleArray with the given size in nibble, filled with the
     * specified nibble
     * value.
     *
     * @param size  The number of nibbles in the array.
     * @param value The value to fill the array with.
     * @throws IllegalArgumentException If size is not positive and even.
     */
    /**
     * Construct a new NibbleArray with the given size in nibbles.
     *
     * @param size The number of nibbles in the array.
     * @throws IllegalArgumentException If size is not positive and even.
     */
    @JvmOverloads
    constructor(size: Int, value: Byte = 0.toByte()) {
        rawData = ByteArray(size / 2)
        if (value.toInt() != 0) fill(value)
    }

    /**
     * Construct a new NibbleArray using the given underlying bytes. No copy is
     * created.
     *
     * @param rawData The raw data to use.
     */
    constructor(vararg rawData: Byte) {
        this.rawData = rawData
    }

    /**
     * Get the size in nibbles.
     *
     * @return The size in nibbles.
     */
    fun size(): Int {
        return 2 * rawData.size
    }

    /**
     * Get the size in bytes, one-half the size in nibbles.
     *
     * @return The size in bytes.
     */
    fun byteSize(): Int {
        return rawData.size
    }

    /**
     * Get the nibble at the given index.
     *
     * @param index The nibble index.
     * @return The value of the nibble at that index.
     */
    fun get(index: Int): Byte {
        val `val` = rawData[index / 2]
        return if (index % 2 == 0) {
            (`val`.toInt() and 0x0f).toByte()
        } else {
            ((`val`.toInt() and 0xf0) shr 4).toByte()
        }
    }

    /**
     * Set the nibble at the given index to the given value.
     *
     * @param index The nibble index.
     * @param value The new value to store.
     */
    operator fun set(index: Int, value: Byte) {
        var value = value
        value = (value.toInt() and 0xf).toByte()
        val half = index / 2
        val previous = rawData[half]
        if (index % 2 == 0) {
            rawData[half] = (previous.toInt() and 0xf0 or value.toInt()).toByte()
        } else {
            rawData[half] = (previous.toInt() and 0x0f or (value.toInt() shl 4)).toByte()
        }
    }

    /**
     * Fill the nibble array with the specified value.
     *
     * @param value The value nibble to fill with.
     */
    private fun fill(value: Byte) {
        var value = value
        value = (value.toInt() and 0xf).toByte()
        Arrays.fill(rawData, (value.toInt() shl 4 or value.toInt()).toByte())
    }

    /**
     * Copies into the raw bytes of this nibble array from the given source.
     *
     * @param source The array to copy from.
     * @throws IllegalArgumentException If source is not the correct length.
     */
    /*
     * public void setRawData(byte... source) {
     * checkArgument(
     * source.length == rawData.length,
     * "expected byte array of length " + rawData.length + ", not " +
     * source.length);
     * System.arraycopy(source, 0, rawData, 0, source.length);
     * }
     */
    /**
     * Take a snapshot of this NibbleArray which will not reflect changes.
     *
     * @return The snapshot NibbleArray.
     */
    fun snapshot(): NibbleArray {
        return NibbleArray(*rawData.clone())
    }

    fun toByteArray(): ByteArray {
        val resultBytes = ByteArray(rawData.size * 2)
        for (i in rawData.indices) {
            resultBytes[i * 2] = (rawData[i].toInt() and 0x0f).toByte()
            resultBytes[i * 2 + 1] = ((rawData[i].toInt() and 0xf0) shr 4).toByte()
        }
        return resultBytes
    }
}