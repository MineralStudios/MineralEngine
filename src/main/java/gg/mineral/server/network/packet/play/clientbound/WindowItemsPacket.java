package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.inventory.item.ItemStack;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public class WindowItemsPacket implements Packet.OUTGOING {
    short windowId;
    ItemStack[] itemstacks;

    public WindowItemsPacket(short windowId, ItemStack[] itemstacks) {
        this.windowId = windowId;
        this.itemstacks = itemstacks;
    }

    @Override
    public void serialize(ByteBuf os) {
        os.writeByte(windowId);
        os.writeShort(itemstacks.length);

        for (ItemStack itemstack : itemstacks) {
            ByteBufUtil.writeSlot(os, itemstack);
        }
    }

    @Override
    public int getId() {
        return 0x30;
    }

}
