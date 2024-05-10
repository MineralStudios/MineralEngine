package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.inventory.item.ItemStack;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public class EntityEquipmentPacket implements Packet.OUTGOING {

    int entityId;
    short slot;
    ItemStack itemStack;

    public EntityEquipmentPacket(int entityId, short slot, ItemStack itemStack) {
        this.entityId = entityId;
        this.slot = slot;
        this.itemStack = itemStack;
    }

    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(entityId);
        os.writeShort(slot);
        ByteBufUtil.writeSlot(os, itemStack);
    }

    @Override
    public byte getId() {
        return 0x04;
    }

}
