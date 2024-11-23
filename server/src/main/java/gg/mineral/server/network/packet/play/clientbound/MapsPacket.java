package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public record MapsPacket(int itemDamage, byte[] data) implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        ByteBufUtil.writeVarInt(os, itemDamage);
        os.writeShort(data.length);
        os.writeBytes(data);
    }

    @Override
    public byte getId() {
        return 0x34;
    }
}
