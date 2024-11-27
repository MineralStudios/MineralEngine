package gg.mineral.api.world.chunk;

public interface Chunk {
    /**
     * Get the type of the block at the specified coordinates.
     * 
     * @param x The local x-coordinate of the block.
     * @param z The local z-coordinate of the block.
     * @param y The local y-coordinate of the block.
     * @return The type of the block at the specified coordinates.
     */
    int getType(int x, int z, short y);

    /**
     * Get the metadata of the block at the specified coordinates.
     * 
     * @param x The local x-coordinate of the block.
     * @param z The local z-coordinate of the block.
     * @param y The local y-coordinate of the block.
     * @return The metadata of the block at the specified coordinates.
     */
    byte getMetaData(int x, int z, short y);

    /**
     * Get the x-coordinate of the chunk.
     * 
     * @return The x-coordinate of the chunk.
     */
    byte getX();

    /**
     * Get the z-coordinate of the chunk.
     * 
     * @return The z-coordinate of the chunk.
     */
    byte getZ();
}
