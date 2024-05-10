package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public class OpenWindowPacket implements Packet.OUTGOING {
    short windowId, inventoryType, numberOfSlots;
    String windowTitle;
    boolean useProvidedWindowTitle;
    int entityId;

    public OpenWindowPacket(short windowId, short inventoryType, short numberOfSlots, String windowTitle,
            boolean useProvidedWindowTitle, int entityId) {
        this.windowId = windowId;
        this.inventoryType = inventoryType;
        this.numberOfSlots = numberOfSlots;
        this.windowTitle = windowTitle;
        this.useProvidedWindowTitle = useProvidedWindowTitle;
        this.entityId = entityId;
    }

    @Override
    public void serialize(ByteBuf os) {
        os.writeByte(windowId);
        os.writeByte(inventoryType);
        ByteBufUtil.writeString(os, windowTitle);
        os.writeByte(numberOfSlots);
        os.writeBoolean(useProvidedWindowTitle);
        os.writeInt(entityId);
    }

    @Override
    public byte getId() {
        return 0x2D;
    }

}
