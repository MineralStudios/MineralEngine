package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public class MapsPacket implements Packet.OUTGOING {
    int itemDamage;
    byte[] data;

    public MapsPacket(int itemDamage, byte[] data) {
        this.itemDamage = itemDamage;
        this.data = data;
    }

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
