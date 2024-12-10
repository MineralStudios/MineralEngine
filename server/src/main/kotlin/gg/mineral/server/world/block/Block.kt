package gg.mineral.server.world.block

import gg.mineral.server.world.chunk.ChunkImpl

class Block(val chunk: ChunkImpl, val x: Int, val y: Int, val z: Int, val type: Int, val data: Byte) {
}
