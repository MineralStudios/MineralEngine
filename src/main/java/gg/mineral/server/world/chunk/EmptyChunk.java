package gg.mineral.server.world.chunk;

import gg.mineral.server.network.packet.play.clientbound.ChunkDataPacket;

import gg.mineral.server.world.IWorld;

public class EmptyChunk extends Chunk {

    public EmptyChunk(IWorld world, byte x, byte z) {
        super(world, x, z);
    }

    @Override
    public ChunkDataPacket toPacket(boolean skylight, boolean entireChunk) {
        return new ChunkDataPacket(this.getX(), this.getZ());
    }
}
