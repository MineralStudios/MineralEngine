package gg.mineral.server.world.chunk;

import gg.mineral.server.network.packet.play.clientbound.ChunkDataPacket;
import gg.mineral.server.world.IWorld.Environment;

public class EmptyChunk extends Chunk {

    public EmptyChunk(Environment environment, byte x, byte z) {
        super(environment, x, z);
    }

    @Override
    public ChunkDataPacket toPacket(boolean skylight, boolean entireChunk) {
        return new ChunkDataPacket(this.getX(), this.getZ());
    }
}
