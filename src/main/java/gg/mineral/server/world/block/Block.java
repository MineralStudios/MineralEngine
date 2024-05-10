package gg.mineral.server.world.block;

import gg.mineral.server.world.chunk.Chunk;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Block {
    final Chunk chunk;
    final int x, y, z;

}
