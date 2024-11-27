package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.api.inventory.item.ItemStack;
import gg.mineral.api.network.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.val;

public final record WindowItemsPacket(short windowId, ItemStack[] itemstacks) implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        os.writeByte(windowId);
        os.writeShort(itemstacks.length);

        for (val itemstack : itemstacks)
            writeSlot(os, itemstack);
    }

    @Override
    public byte getId() {
        return 0x30;
    }
}
