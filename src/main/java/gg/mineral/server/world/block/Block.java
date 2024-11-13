package gg.mineral.server.world.block;

import gg.mineral.server.world.chunk.Chunk;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@RequiredArgsConstructor
@Value
public class Block {
    Chunk chunk;
    int x, y, z;
}
