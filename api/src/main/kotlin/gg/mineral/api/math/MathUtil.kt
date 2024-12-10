package gg.mineral.api.math

object MathUtil {
    fun toFixedPointInt(value: Double): Int = (value * 32).toInt()

    fun toFixedPointByte(value: Double): Byte = (value * 32).toInt().toByte()

    fun floor(num: Double): Int {
        val floor = num.toInt()
        return if (floor.toDouble() == num) floor else floor - (java.lang.Double.doubleToRawLongBits(num) ushr 63).toInt()
    }

    fun ceil(num: Double): Int {
        val floor = num.toInt()
        return if (floor.toDouble() == num) floor else floor + (java.lang.Double.doubleToRawLongBits(num)
            .inv() ushr 63).toInt()
    }

    fun round(num: Double): Int = floor(num + 0.5)

    fun square(num: Double): Double = num * num

    fun sin(num: Double): Double = kotlin.math.sin(num)

    fun cos(num: Double): Double = kotlin.math.cos(num)

    fun toRadians(angle: Float): Float = Math.toRadians(angle.toDouble()).toFloat()

    fun toInt(`object`: Any?): Int {
        if (`object` is Number) return `object`.toInt()

        if (`object` == null) return 0

        try {
            return `object`.toString().toInt()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
        return 0
    }

    fun toFloat(`object`: Any?): Float {
        if (`object` is Number) return `object`.toFloat()

        if (`object` == null) return 0f

        try {
            return `object`.toString().toFloat()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
        return 0f
    }

    fun toDouble(`object`: Any?): Double {
        if (`object` is Number) return `object`.toDouble()

        if (`object` == null) return 0.0

        try {
            return `object`.toString().toDouble()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
        return 0.0
    }

    fun toLong(`object`: Any?): Long {
        if (`object` is Number) return `object`.toLong()

        if (`object` == null) return 0

        try {
            return `object`.toString().toLong()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
        return 0
    }

    fun toShort(`object`: Any?): Short {
        if (`object` is Number) return `object`.toShort()

        if (`object` == null) return 0

        try {
            return `object`.toString().toShort()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
        return 0
    }

    fun toByte(`object`: Any?): Byte {
        if (`object` is Number) return `object`.toByte()

        if (`object` == null) return 0
        try {
            return `object`.toString().toByte()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
        return 0
    }

    fun angleToByte(angle: Float): Byte = (angle * 256 / 360).toInt().toByte()

    fun toVelocityUnits(value: Double): Short = (value * 8000.0).toInt().toShort()

    fun fromVelocityUnits(value: Short): Double = value / 8000.0

    fun toSoundUnits(value: Double): Int = (value * 8).toInt()

    fun fromSoundUnits(value: Int): Double = value / 8.0

    fun toPitchUnits(value: Double): Short = (value * 63).toInt().toShort()

    fun fromPitchUnits(value: Short): Double = value / 63.0

    fun unsigned(s: Short): Int = s.toInt() and 0xFFFF
}
