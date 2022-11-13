package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.world.chunk.Meta;
import io.netty.buffer.ByteBuf;

public class MapChunkBulkPacket implements Packet.OUTGOING {
    short chunkColumnCount;
    boolean skyLightSent;
    byte[] data;
    Meta metaInformation;

    public MapChunkBulkPacket(short chunkColumnCount, boolean skyLightSent, byte[] data, Meta metaInformation) {
        this.chunkColumnCount = chunkColumnCount;
        this.skyLightSent = skyLightSent;
        this.data = data;
        this.metaInformation = metaInformation;
    }

    @Override
    public void serialize(ByteBuf os) {
        os.writeShort(chunkColumnCount);
        os.writeInt(data.length);
        os.writeBoolean(skyLightSent);
        os.writeBytes(data);
        os.writeInt(metaInformation.getChunkX());
        os.writeInt(metaInformation.getChunkZ());
        os.writeShort(metaInformation.getPrimaryBitMap());
        os.writeShort(metaInformation.getAddBitMap());
    }

    @Override
    public int getId() {
        return 0x26;
    }

}
