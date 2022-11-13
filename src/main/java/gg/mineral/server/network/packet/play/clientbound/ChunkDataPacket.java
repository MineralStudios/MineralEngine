package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public class ChunkDataPacket implements Packet.OUTGOING {

    int chunkX, chunkZ, primaryBitMap, addBitMap;
    byte[] compressedData;
    boolean groundUpContinuous;

    public ChunkDataPacket(int chunkX, int chunkZ, int primaryBitMap, int addBitMap,
            byte[] compressedData, boolean groundUpContinuous) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.primaryBitMap = primaryBitMap;
        this.addBitMap = addBitMap;
        this.groundUpContinuous = groundUpContinuous;
        this.compressedData = compressedData;
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
    public int getId() {
        return 0x21;
    }

}
