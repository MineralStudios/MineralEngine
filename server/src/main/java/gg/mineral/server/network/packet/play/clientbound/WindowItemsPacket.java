package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.inventory.item.ItemStack;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;
import lombok.val;

public record WindowItemsPacket(short windowId, ItemStack[] itemstacks) implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        os.writeByte(windowId);
        os.writeShort(itemstacks.length);

        for (val itemstack : itemstacks)
            ByteBufUtil.writeSlot(os, itemstack);
    }

    @Override
    public byte getId() {
        return 0x30;
    }
}
