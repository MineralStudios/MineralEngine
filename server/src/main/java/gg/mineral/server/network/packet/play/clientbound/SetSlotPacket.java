package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.inventory.item.ItemStack;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public record SetSlotPacket(byte windowId, short slot, ItemStack itemStack) implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        os.writeByte(windowId);
        os.writeShort(slot);
        ByteBufUtil.writeSlot(os, itemStack);
    }

    @Override
    public byte getId() {
        return 0x2F;
    }
}
