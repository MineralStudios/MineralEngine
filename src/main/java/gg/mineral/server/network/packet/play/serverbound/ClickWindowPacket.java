package gg.mineral.server.network.packet.play.serverbound;

import gg.mineral.server.inventory.item.ItemStack;
import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public class ClickWindowPacket implements Packet.INCOMING {
    byte windowId, button, mode;
    short slot, actionNumber;
    ItemStack clickedItem;

    @Override
    public void received(Connection connection) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deserialize(ByteBuf is) {
        windowId = is.readByte();
        slot = is.readShort();
        button = is.readByte();
        actionNumber = is.readShort();
        mode = is.readByte();
        clickedItem = ByteBufUtil.readSlot(is);
    }

}
