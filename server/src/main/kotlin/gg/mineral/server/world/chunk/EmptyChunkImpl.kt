package gg.mineral.server.world.chunk

import gg.mineral.api.world.World
import gg.mineral.api.world.chunk.EmptyChunk
import gg.mineral.server.network.packet.play.clientbound.ChunkDataPacket

class EmptyChunkImpl(world: World, x: Byte, z: Byte) : ChunkImpl(world, x, z), EmptyChunk {
    private val packet by lazy { ChunkDataPacket(this.x.toInt(), this.z.toInt()) }

    override fun toPacket(skylight: Boolean, compress: Boolean): ChunkDataPacket {
        return packet
    }
}
