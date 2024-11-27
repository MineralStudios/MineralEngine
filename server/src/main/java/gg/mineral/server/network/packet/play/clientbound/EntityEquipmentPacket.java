package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.api.inventory.item.ItemStack;
import gg.mineral.api.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public final record EntityEquipmentPacket(int entityId, short slot, ItemStack itemStack)
        implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(entityId);
        os.writeShort(slot);
        writeSlot(os, itemStack);
    }

    @Override
    public byte getId() {
        return 0x04;
    }
}
