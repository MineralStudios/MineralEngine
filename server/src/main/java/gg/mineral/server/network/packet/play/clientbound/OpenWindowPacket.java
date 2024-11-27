package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.api.network.packet.Packet;

import io.netty.buffer.ByteBuf;

public final record OpenWindowPacket(short windowId, short inventoryType, String windowTitle, short numberOfSlots,
        boolean useProvidedWindowTitle, int entityId) implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        os.writeByte(windowId);
        os.writeByte(inventoryType);
        writeString(os, windowTitle);
        os.writeByte(numberOfSlots);
        os.writeBoolean(useProvidedWindowTitle);
        os.writeInt(entityId);
    }

    @Override
    public byte getId() {
        return 0x2D;
    }
}
