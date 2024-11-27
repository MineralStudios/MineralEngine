package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.api.network.packet.Packet;

import io.netty.buffer.ByteBuf;

public final record MapsPacket(int itemDamage, byte[] data) implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        writeVarInt(os, itemDamage);
        os.writeShort(data.length);
        os.writeBytes(data);
    }

    @Override
    public byte getId() {
        return 0x34;
    }
}
