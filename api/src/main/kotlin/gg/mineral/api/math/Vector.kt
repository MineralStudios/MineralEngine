package gg.mineral.api.math

import gg.mineral.api.math.MathUtil.floor
import gg.mineral.api.math.MathUtil.square
import gg.mineral.api.world.World
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.sqrt

/**
 * Represents a mutable vector. Because the components of Vectors are mutable,
 * storing Vectors long term may be dangerous if passing code modifies the
 * Vector later. If you want to keep around a Vector, it may be wise to call
 * `clone()` in order to get a copy.
 */
open class Vector : Cloneable {
    /**
     * Gets the X component.
     *
     * @return The X component.
     */
    var x: Double
        protected set

    /**
     * Gets the Y component.
     *
     * @return The Y component.
     */
    var y: Double
        protected set

    /**
     * Gets the Z component.
     *
     * @return The Z component.
     */
    var z: Double
        protected set

    /**
     * Construct the vector with all components as 0.
     */
    constructor() {
        this.x = 0.0
        this.y = 0.0
        this.z = 0.0
    }

    /**
     * Construct the vector with provided integer components.
     *
     * @param x X component
     * @param y Y component
     * @param z Z component
     */
    constructor(x: Int, y: Int, z: Int) {
        this.x = x.toDouble()
        this.y = y.toDouble()
        this.z = z.toDouble()
    }

    /**
     * Construct the vector with provided double components.
     *
     * @param x X component
     * @param y Y component
     * @param z Z component
     */
    constructor(x: Double, y: Double, z: Double) {
        this.x = x
        this.y = y
        this.z = z
    }

    /**
     * Construct the vector with provided float components.
     *
     * @param x X component
     * @param y Y component
     * @param z Z component
     */
    constructor(x: Float, y: Float, z: Float) {
        this.x = x.toDouble()
        this.y = y.toDouble()
        this.z = z.toDouble()
    }

    /**
     * Adds a vector to this one
     *
     * @param vec The other vector
     * @return the same vector
     */
    fun add(vec: Vector): Vector {
        x += vec.x
        y += vec.y
        z += vec.z
        return this
    }

    /**
     * Subtracts a vector from this one.
     *
     * @param vec The other vector
     * @return the same vector
     */
    fun subtract(vec: Vector): Vector {
        x -= vec.x
        y -= vec.y
        z -= vec.z
        return this
    }

    /**
     * Multiplies the vector by another.
     *
     * @param vec The other vector
     * @return the same vector
     */
    fun multiply(vec: Vector): Vector {
        x *= vec.x
        y *= vec.y
        z *= vec.z
        return this
    }

    /**
     * Divides the vector by another.
     *
     * @param vec The other vector
     * @return the same vector
     */
    fun divide(vec: Vector): Vector {
        x /= vec.x
        y /= vec.y
        z /= vec.z
        return this
    }

    /**
     * Copies another vector
     *
     * @param vec The other vector
     * @return the same vector
     */
    fun copy(vec: Vector): Vector {
        x = vec.x
        y = vec.y
        z = vec.z
        return this
    }

    /**
     * Gets the magnitude of the vector, defined as sqrt(x^2+y^2+z^2). The
     * value of this method is not cached and uses a costly square-root
     * function, so do not repeatedly call this method to get the vector's
     * magnitude. NaN will be returned if the inner result of the sqrt()
     * function overflows, which will be caused if the length is too long.
     *
     * @return the magnitude
     */
    fun length(): Double {
        return sqrt(square(x) + square(y) + square(z))
    }

    /**
     * Gets the magnitude of the vector squared.
     *
     * @return the magnitude
     */
    fun lengthSquared(): Double {
        return square(x) + square(y) + square(z)
    }

    /**
     * Get the distance between this vector and another. The value of this
     * method is not cached and uses a costly square-root function, so do not
     * repeatedly call this method to get the vector's magnitude. NaN will be
     * returned if the inner result of the sqrt() function overflows, which
     * will be caused if the distance is too long.
     *
     * @param o The other vector
     * @return the distance
     */
    fun distance(o: Vector): Double {
        return sqrt(square(x - o.x) + square(y - o.y) + square(z - o.z))
    }

    /**
     * Get the squared distance between this vector and another.
     *
     * @param o The other vector
     * @return the distance
     */
    fun distanceSquared(o: Vector): Double {
        return square(x - o.x) + square(y - o.y) + square(z - o.z)
    }

    /**
     * Gets the angle between this vector and another in radians.
     *
     * @param other The other vector
     * @return angle in radians
     */
    fun angle(other: Vector): Float {
        val dot = dot(other) / (length() * other.length())

        return acos(dot).toFloat()
    }

    /**
     * Sets this vector to the midpoint between this vector and another.
     *
     * @param other The other vector
     * @return this same vector (now a midpoint)
     */
    fun midpoint(other: Vector): Vector {
        x = (x + other.x) / 2
        y = (y + other.y) / 2
        z = (z + other.z) / 2
        return this
    }

    /**
     * Gets a new midpoint vector between this vector and another.
     *
     * @param other The other vector
     * @return a new midpoint vector
     */
    fun getMidpoint(other: Vector): Vector {
        val x = (this.x + other.x) / 2
        val y = (this.y + other.y) / 2
        val z = (this.z + other.z) / 2
        return Vector(x, y, z)
    }

    /**
     * Performs scalar multiplication, multiplying all components with a
     * scalar.
     *
     * @param m The factor
     * @return the same vector
     */
    fun multiply(m: Int): Vector {
        x *= m.toDouble()
        y *= m.toDouble()
        z *= m.toDouble()
        return this
    }

    /**
     * Performs scalar multiplication, multiplying all components with a
     * scalar.
     *
     * @param m The factor
     * @return the same vector
     */
    fun multiply(m: Double): Vector {
        x *= m
        y *= m
        z *= m
        return this
    }

    /**
     * Performs scalar multiplication, multiplying all components with a
     * scalar.
     *
     * @param m The factor
     * @return the same vector
     */
    fun multiply(m: Float): Vector {
        x *= m.toDouble()
        y *= m.toDouble()
        z *= m.toDouble()
        return this
    }

    /**
     * Calculates the dot product of this vector with another. The dot product
     * is defined as x1*x2+y1*y2+z1*z2. The returned value is a scalar.
     *
     * @param other The other vector
     * @return dot product
     */
    fun dot(other: Vector): Double {
        return x * other.x + y * other.y + z * other.z
    }

    /**
     * Calculates the cross product of this vector with another. The cross
     * product is defined as:
     *
     *  * x = y1 * z2 - y2 * z1
     *  * y = z1 * x2 - z2 * x1
     *  * z = x1 * y2 - x2 * y1
     *
     *
     * @param o The other vector
     * @return the same vector
     */
    fun crossProduct(o: Vector): Vector {
        val newX = y * o.z - o.y * z
        val newY = z * o.x - o.z * x
        val newZ = x * o.y - o.x * y

        x = newX
        y = newY
        z = newZ
        return this
    }

    /**
     * Converts this vector to a unit vector (a vector with length of 1).
     *
     * @return the same vector
     */
    fun normalize(): Vector {
        val length = length()

        x /= length
        y /= length
        z /= length

        return this
    }

    /**
     * Zero this vector's components.
     *
     * @return the same vector
     */
    fun zero(): Vector {
        x = 0.0
        y = 0.0
        z = 0.0
        return this
    }

    /**
     * Returns whether this vector is in an axis-aligned bounding box.
     *
     *
     * The minimum and maximum vectors given must be truly the minimum and
     * maximum X, Y and Z components.
     *
     * @param min Minimum vector
     * @param max Maximum vector
     * @return whether this vector is in the AABB
     */
    fun isInAABB(min: Vector, max: Vector): Boolean {
        return x >= min.x && x <= max.x && y >= min.y && y <= max.y && z >= min.z && z <= max.z
    }

    /**
     * Returns whether this vector is within a sphere.
     *
     * @param origin Sphere origin.
     * @param radius Sphere radius
     * @return whether this vector is in the sphere
     */
    fun isInSphere(origin: Vector, radius: Double): Boolean {
        return ((square(origin.x - x) + square(origin.y - y)
                + square(origin.z - z))) <= square(radius)
    }

    /**
     * Set the X component.
     *
     * @param x The new X component.
     * @return This vector.
     */
    fun setX(x: Int): Vector {
        this.x = x.toDouble()
        return this
    }

    /**
     * Set the X component.
     *
     * @param x The new X component.
     * @return This vector.
     */
    fun setX(x: Double): Vector {
        this.x = x
        return this
    }

    /**
     * Set the X component.
     *
     * @param x The new X component.
     * @return This vector.
     */
    fun setX(x: Float): Vector {
        this.x = x.toDouble()
        return this
    }

    val blockX: Int
        /**
         * Gets the floored value of the X component, indicating the block that
         * this vector is contained with.
         *
         * @return block X
         */
        get() = floor(x)

    /**
     * Set the Y component.
     *
     * @param y The new Y component.
     * @return This vector.
     */
    fun setY(y: Int): Vector {
        this.y = y.toDouble()
        return this
    }

    /**
     * Set the Y component.
     *
     * @param y The new Y component.
     * @return This vector.
     */
    fun setY(y: Double): Vector {
        this.y = y
        return this
    }

    /**
     * Set the Y component.
     *
     * @param y The new Y component.
     * @return This vector.
     */
    fun setY(y: Float): Vector {
        this.y = y.toDouble()
        return this
    }

    val blockY: Int
        /**
         * Gets the floored value of the Y component, indicating the block that
         * this vector is contained with.
         *
         * @return block y
         */
        get() = floor(y)

    /**
     * Set the Z component.
     *
     * @param z The new Z component.
     * @return This vector.
     */
    fun setZ(z: Int): Vector {
        this.z = z.toDouble()
        return this
    }

    /**
     * Set the Z component.
     *
     * @param z The new Z component.
     * @return This vector.
     */
    fun setZ(z: Double): Vector {
        this.z = z
        return this
    }

    /**
     * Set the Z component.
     *
     * @param z The new Z component.
     * @return This vector.
     */
    fun setZ(z: Float): Vector {
        this.z = z.toDouble()
        return this
    }

    val blockZ: Int
        /**
         * Gets the floored value of the Z component, indicating the block that
         * this vector is contained with.
         *
         * @return block z
         */
        get() = floor(z)

    /**
     * Checks to see if two objects are equal.
     *
     *
     * Only two Vectors can ever return true. This method uses a fuzzy match
     * to account for floating point errors. The epsilon can be retrieved
     * with epsilon.
     */
    override fun equals(other: Any?): Boolean {
        if (other !is Vector)
            return false

        return abs(x - other.x) < epsilon && abs(y - other.y) < epsilon && abs(z - other.z) < epsilon && (this.javaClass == other.javaClass)
    }

    /**
     * Returns a hash code for this vector
     *
     * @return hash code
     */
    override fun hashCode(): Int {
        var hash = 7

        hash =
            79 * hash + (java.lang.Double.doubleToLongBits(this.x) xor (java.lang.Double.doubleToLongBits(this.x) ushr 32)).toInt()
        hash =
            79 * hash + (java.lang.Double.doubleToLongBits(this.y) xor (java.lang.Double.doubleToLongBits(this.y) ushr 32)).toInt()
        hash =
            79 * hash + (java.lang.Double.doubleToLongBits(this.z) xor (java.lang.Double.doubleToLongBits(this.z) ushr 32)).toInt()
        return hash
    }

    /**
     * Get a new vector.
     *
     * @return vector
     */
    public override fun clone(): Vector {
        try {
            return super.clone() as Vector
        } catch (e: CloneNotSupportedException) {
            throw Error(e)
        }
    }

    /**
     * Returns this vector's components as x,y,z.
     */
    override fun toString(): String {
        return "$x,$y,$z"
    }

    /**
     * Gets a Location version of this vector with yaw and pitch being 0.
     *
     * @param world The world to link the location to.
     * @return the location
     */
    fun toLocation(world: World): Location {
        return Location(world, x, y, z)
    }

    /**
     * Gets a Location version of this vector.
     *
     * @param world The world to link the location to.
     * @param yaw   The desired yaw.
     * @param pitch The desired pitch.
     * @return the location
     */
    fun toLocation(world: World, yaw: Float, pitch: Float): Location {
        return Location(world, x, y, z, yaw, pitch)
    }

    /**
     * Get the block vector of this vector.
     *
     * @return A block vector.
     */
    fun toBlockVector(): BlockVector {
        return BlockVector(x, y, z)
    }

    fun serialize(): Map<String, Any> {
        val result: MutableMap<String, Any> = LinkedHashMap()

        result["x"] = x
        result["y"] = y
        result["z"] = z

        return result
    }

    companion object {
        /**
         * Get the threshold used for equals().
         *
         * @return The epsilon.
         */
        /**
         * Threshold for fuzzy equals().
         */
        const val epsilon: Double = 0.000001

        /**
         * Gets the minimum components of two vectors.
         *
         * @param v1 The first vector.
         * @param v2 The second vector.
         * @return minimum
         */
        fun getMinimum(v1: Vector, v2: Vector): Vector {
            return Vector(v1.x.coerceAtMost(v2.x), v1.y.coerceAtMost(v2.y), v1.z.coerceAtMost(v2.z))
        }

        /**
         * Gets the maximum components of two vectors.
         *
         * @param v1 The first vector.
         * @param v2 The second vector.
         * @return maximum
         */
        fun getMaximum(v1: Vector, v2: Vector): Vector {
            return Vector(v1.x.coerceAtLeast(v2.x), v1.y.coerceAtLeast(v2.y), v1.z.coerceAtLeast(v2.z))
        }

        val random: Vector
            /**
             * Gets a random vector with components having a random value between 0
             * and 1.
             *
             * @return A random vector.
             */
            get() {
                val random = ThreadLocalRandom.current()
                return Vector(random.nextDouble(), random.nextDouble(), random.nextDouble())
            }

        fun deserialize(args: Map<String?, Any?>): Vector {
            var x = 0.0
            var y = 0.0
            var z = 0.0

            if (args.containsKey("x"))
                x = (args["x"] as Double?)!!
            
            if (args.containsKey("y"))
                y = (args["y"] as Double?)!!

            if (args.containsKey("z"))
                z = (args["z"] as Double?)!!


            return Vector(x, y, z)
        }
    }
}
