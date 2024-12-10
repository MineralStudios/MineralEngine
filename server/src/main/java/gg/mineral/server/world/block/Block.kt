package gg.mineral.server.world.block

import gg.mineral.server.world.chunk.ChunkImpl
import lombok.RequiredArgsConstructor
import lombok.Value

@RequiredArgsConstructor
@Value
class Block {
    var chunk: ChunkImpl? = null
    var x: Int = 0
    var y: Int = 0
    var z: Int = 0
    var type: Int = 0
    var data: Byte = 0
}
