package gg.mineral.server.world.chunk;

public class Meta {
    int chunkX, chunkZ, primaryBitMap, addBitMap;

    public Meta(int chunkX, int chunkZ, int primaryBitMap, int addBitMap) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.primaryBitMap = primaryBitMap;
        this.addBitMap = addBitMap;
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }

    public int getPrimaryBitMap() {
        return primaryBitMap;
    }

    public int getAddBitMap() {
        return addBitMap;
    }
}
