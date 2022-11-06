package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public class ChunkDataPacket implements Packet.OUTGOING {

    int chunkX, chunkZ, compressedSize, primaryBitMap, addBitMap;
    byte[] compressedData;
    boolean groundUpContinuous;

    public ChunkDataPacket(int chunkX, int chunkZ, int compresserSize, int primaryBitMap, int addBitMap,
            byte[] compressedData, boolean groundUpContinuous) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.compressedSize = compresserSize;
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
        os.writeInt(compressedSize);
        ByteBufUtil.writeBytes(os, compressedData);
    }

    @Override
    public int getId() {
        return 0x21;
    }

}
