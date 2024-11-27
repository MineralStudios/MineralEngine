package gg.mineral.server.world.chunk;

import gg.mineral.api.world.World;
import gg.mineral.api.world.chunk.EmptyChunk;
import gg.mineral.server.network.packet.play.clientbound.ChunkDataPacket;

public class EmptyChunkImpl extends ChunkImpl implements EmptyChunk {

    private final ChunkDataPacket packet;

    public EmptyChunkImpl(World world, byte x, byte z) {
        super(world, x, z);
        this.packet = new ChunkDataPacket(this.getX(), this.getZ());
    }

    @Override
    public ChunkDataPacket toPacket(boolean skylight, boolean entireChunk) {
        return packet;
    }
}
