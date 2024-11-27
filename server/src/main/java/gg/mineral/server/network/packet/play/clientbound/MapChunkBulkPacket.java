package gg.mineral.server.network.packet.play.clientbound;

import java.util.List;

import gg.mineral.api.world.chunk.Chunk;
import gg.mineral.api.network.packet.Packet;
import gg.mineral.server.world.chunk.ChunkImpl;
import io.netty.buffer.ByteBuf;
import lombok.val;

public final record MapChunkBulkPacket(boolean skyLight, List<Chunk> chunks) implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        int amount = this.chunks.size();
        val bytes = new byte[196864 * amount];
        int bytesPosition = 0;

        val output = new Pair[amount];
        for (int i = 0; i < amount; i++) {
            if (this.chunks.get(i) instanceof ChunkImpl chunk) {
                val out = chunk.toPacket(skyLight, false);
                System.arraycopy(out.compressedData(), 0, bytes, bytesPosition, out.compressedData().length);
                bytesPosition += out.compressedData().length;
                output[i] = new Pair(chunk, out);
            }
        }

        val compressed = ChunkImpl.compress(bytes, bytesPosition);

        os.writeShort(amount);
        os.writeInt(compressed.length);
        os.writeBoolean(skyLight);
        os.writeBytes(compressed);

        for (val pair : output) {
            val col = pair.chunk();
            val out = pair.packet();
            os.writeInt(col.getX());
            os.writeInt(col.getZ());
            os.writeShort(out.primaryBitMap());
            os.writeShort(out.addBitMap());
        }
    }

    private record Pair(Chunk chunk, ChunkDataPacket packet) {
    }

    @Override
    public byte getId() {
        return 0x26;
    }

}
