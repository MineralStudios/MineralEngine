package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public record ChunkDataPacket(int chunkX, int chunkZ, boolean groundUpContinuous, int primaryBitMap, int addBitMap,
        byte[] compressedData) implements Packet.OUTGOING {
    public ChunkDataPacket(int chunkX, int chunkZ) {
        this(chunkX, chunkZ, true, 0, 0, new byte[0]);
    }

    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(chunkX);
        os.writeInt(chunkZ);
        os.writeBoolean(groundUpContinuous);
        os.writeShort(primaryBitMap);
        os.writeShort(addBitMap);
        os.writeInt(compressedData.length);
        os.writeBytes(compressedData);
    }

    @Override
    public byte getId() {
        return 0x21;
    }

}
