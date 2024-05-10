package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ChunkDataPacket implements Packet.OUTGOING {
    final int chunkX, chunkZ, primaryBitMap, addBitMap;
    final boolean groundUpContinuous;
    final byte[] compressedData;

    public ChunkDataPacket(int chunkX, int chunkZ) {
        this(chunkX, chunkZ, 0, 0, true, new byte[0]);
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
