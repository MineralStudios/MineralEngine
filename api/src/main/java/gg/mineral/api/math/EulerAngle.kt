package gg.mineral.api.math

import lombok.Getter
import lombok.RequiredArgsConstructor

/**
 * EulerAngle is used to represent 3 angles, one for each
 * axis (x, y, z). The angles are in radians
 */
@RequiredArgsConstructor
@Getter
class EulerAngle {
    private val x = 0.0
    private val y = 0.0
    private val z = 0.0

    /**
     * Return a EulerAngle which is the result of changing
     * the x axis to the passed angle
     *
     * @param x the angle in radians
     * @return the resultant EulerAngle
     */
    fun setX(x: Double): EulerAngle {
        return EulerAngle(x, y, z)
    }

    /**
     * Return a EulerAngle which is the result of changing
     * the y axis to the passed angle
     *
     * @param y the angle in radians
     * @return the resultant EulerAngle
     */
    fun setY(y: Double): EulerAngle {
        return EulerAngle(x, y, z)
    }

    /**
     * Return a EulerAngle which is the result of changing
     * the z axis to the passed angle
     *
     * @param z the angle in radians
     * @return the resultant EulerAngle
     */
    fun setZ(z: Double): EulerAngle {
        return EulerAngle(x, y, z)
    }

    /**
     * Creates a new EulerAngle which is the result of adding
     * the x, y, z components to this EulerAngle
     *
     * @param x the angle to add to the x axis in radians
     * @param y the angle to add to the y axis in radians
     * @param z the angle to add to the z axis in radians
     * @return the resultant EulerAngle
     */
    fun add(x: Double, y: Double, z: Double): EulerAngle {
        return EulerAngle(
            this.x + x,
            this.y + y,
            this.z + z
        )
    }

    /**
     * Creates a new EulerAngle which is the result of subtracting
     * the x, y, z components to this EulerAngle
     *
     * @param x the angle to subtract to the x axis in radians
     * @param y the angle to subtract to the y axis in radians
     * @param z the angle to subtract to the z axis in radians
     * @return the resultant EulerAngle
     */
    fun subtract(x: Double, y: Double, z: Double): EulerAngle {
        return add(-x, -y, -z)
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val that = o as EulerAngle

        return java.lang.Double.compare(that.x, x) == 0 && java.lang.Double.compare(
            that.y,
            y
        ) == 0 && java.lang.Double.compare(that.z, z) == 0
    }

    override fun hashCode(): Int {
        var result: Int
        var temp = java.lang.Double.doubleToLongBits(x)
        result = (temp xor (temp ushr 32)).toInt()
        temp = java.lang.Double.doubleToLongBits(y)
        result = 31 * result + (temp xor (temp ushr 32)).toInt()
        temp = java.lang.Double.doubleToLongBits(z)
        result = 31 * result + (temp xor (temp ushr 32)).toInt()
        return result
    }

    companion object {
        /**
         * A EulerAngle with every axis set to 0
         */
        val ZERO: EulerAngle = EulerAngle(0.0, 0.0, 0.0)
    }
}