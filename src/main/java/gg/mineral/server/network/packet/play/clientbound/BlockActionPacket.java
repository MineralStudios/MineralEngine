package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class BlockActionPacket implements Packet.OUTGOING {
    int x;
    short y;
    int z, blockType;
    short byte1, byte2;

    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(x);
        os.writeShort(y);
        os.writeInt(z);
        os.writeByte(byte1);
        os.writeByte(byte2);
        ByteBufUtil.writeVarInt(os, blockType);
    }

    @Override
    public byte getId() {
        return 0x24;
    }

}
