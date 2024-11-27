package gg.mineral.server.world.schematic;

import java.io.File;
import java.util.Collections;
import java.util.List;

import gg.mineral.server.world.chunk.ChunkImpl;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class SchematicFile {
    private final Short2ObjectOpenHashMap<List<SchematicBlock>> chunkedBlocks = new Short2ObjectOpenHashMap<>();
    private final File source;
    @Setter
    private short xSize, ySize, zSize;

    public List<SchematicBlock> getBlocksForChunk(byte chunkX, byte chunkZ) {
        short key = ChunkImpl.toKey(chunkX, chunkZ);
        return chunkedBlocks.getOrDefault(key, Collections.emptyList());
    }
}