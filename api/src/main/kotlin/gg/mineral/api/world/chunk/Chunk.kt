package gg.mineral.api.world.chunk

interface Chunk {
    /**
     * Get the type of the block at the specified coordinates.
     *
     * @param x The local x-coordinate of the block.
     * @param z The local z-coordinate of the block.
     * @param y The local y-coordinate of the block.
     * @return The type of the block at the specified coordinates.
     */
    fun getType(x: Int, z: Int, y: Short): Int

    /**
     * Get the metadata of the block at the specified coordinates.
     *
     * @param x The local x-coordinate of the block.
     * @param z The local z-coordinate of the block.
     * @param y The local y-coordinate of the block.
     * @return The metadata of the block at the specified coordinates.
     */
    fun getMetaData(x: Int, z: Int, y: Short): Byte

    /**
     * Get the x-coordinate of the chunk.
     *
     * @return The x-coordinate of the chunk.
     */
    val x: Byte

    /**
     * Get the z-coordinate of the chunk.
     *
     * @return The z-coordinate of the chunk.
     */
    val z: Byte
}
