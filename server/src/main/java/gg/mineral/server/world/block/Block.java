package gg.mineral.server.world.block;

import gg.mineral.server.world.chunk.ChunkImpl;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@RequiredArgsConstructor
@Value
public class Block {
    ChunkImpl chunk;
    int x, y, z, type;
    byte data;
}
