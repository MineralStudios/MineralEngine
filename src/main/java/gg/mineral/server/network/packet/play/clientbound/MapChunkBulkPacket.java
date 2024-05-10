package gg.mineral.server.network.packet.play.clientbound;

import java.util.List;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.world.chunk.Chunk;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class MapChunkBulkPacket implements Packet.OUTGOING {

    private final Chunk[] columns;
    private final boolean skyLight;

    public MapChunkBulkPacket(boolean skyLight, List<Chunk> chunks) {
        this.skyLight = skyLight;
        this.columns = chunks.toArray(new Chunk[0]);
    }

    @Override
    public void serialize(ByteBuf os) {
        byte[] bytes = new byte[196864 * columns.length];
        int bytesPosition = 0;

        Pair[] output = new Pair[this.columns.length];
        for (int i = 0; i < this.columns.length; i++) {
            Chunk column = this.columns[i];
            ChunkDataPacket out = column.toDecompressedPacket(skyLight);
            System.arraycopy(out.getCompressedData(), 0, bytes, bytesPosition, out.getCompressedData().length);
            bytesPosition += out.getCompressedData().length;
            output[i] = new Pair(column, out);
        }

        byte[] compressed = Chunk.compress(bytes, bytesPosition);

        os.writeShort(columns.length);
        os.writeInt(compressed.length);
        os.writeBoolean(skyLight);
        os.writeBytes(compressed);

        for (Pair pair : output) {
            Chunk col = pair.getChunk();
            ChunkDataPacket out = pair.getPacket();
            os.writeInt(col.getX());
            os.writeInt(col.getZ());
            os.writeShort(out.getPrimaryBitMap());
            os.writeShort(out.getAddBitMap());
        }
    }

    @Getter
    @RequiredArgsConstructor
    private class Pair {
        private final Chunk chunk;
        private final ChunkDataPacket packet;
    }

    @Override
    public byte getId() {
        return 0x26;
    }

}
