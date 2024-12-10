package gg.mineral.api.math

/**
 * A vector with a hash function that floors the X, Y, Z components, a la
 * BlockVector in WorldEdit. BlockVectors can be used in hash sets and
 * hash maps. Be aware that BlockVectors are mutable, but it is important
 * that BlockVectors are never changed once put into a hash set or hash map.
 */
class BlockVector : Vector {
    /**
     * Construct the vector with all components as 0.
     */
    constructor() {
        this.x = 0.0
        this.y = 0.0
        this.z = 0.0
    }

    /**
     * Construct the vector with another vector.
     *
     * @param vec The other vector.
     */
    constructor(vec: Vector) {
        this.x = vec.getX()
        this.y = vec.getY()
        this.z = vec.getZ()
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
     * Checks if another object is equivalent.
     *
     * @param obj The other object
     * @return whether the other object is equivalent
     */
    override fun equals(obj: Any?): Boolean {
        if (obj !is BlockVector) return false

        val other = obj

        return other.getX().toInt() == x.toInt() && other.getY().toInt() == y.toInt() && other.getZ()
            .toInt() == z.toInt()
    }

    /**
     * Returns a hash code for this vector.
     *
     * @return hash code
     */
    override fun hashCode(): Int {
        return ((x.toInt().hashCode() shr 13) xor (y.toInt().hashCode() shr 7)
                xor z.toInt().hashCode())
    }

    /**
     * Get a new block vector.
     *
     * @return vector
     */
    override fun clone(): BlockVector {
        return super.clone() as BlockVector
    }

    companion object {
        fun deserialize(args: Map<String?, Any?>): BlockVector {
            var x = 0.0
            var y = 0.0
            var z = 0.0

            if (args.containsKey("x")) x = (args["x"] as Double?)!!

            if (args.containsKey("y")) y = (args["y"] as Double?)!!

            if (args.containsKey("z")) z = (args["z"] as Double?)!!

            return BlockVector(x, y, z)
        }
    }
}