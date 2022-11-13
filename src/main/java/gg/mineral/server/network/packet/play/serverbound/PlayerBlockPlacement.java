package gg.mineral.server.network.packet.play.serverbound;

import gg.mineral.server.inventory.item.ItemStack;
import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public class PlayerBlockPlacement implements Packet.INCOMING {
    int x, z;
    short y;
    byte direction, cursorX, cursorY, cursorZ;
    ItemStack itemStack;

    @Override
    public void received(Connection connection) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deserialize(ByteBuf is) {
        x = is.readInt();
        y = is.readUnsignedByte();
        z = is.readInt();
        direction = is.readByte();
        itemStack = ByteBufUtil.readSlot(is);
        cursorX = is.readByte();
        cursorY = is.readByte();
        cursorZ = is.readByte();
    }

    @Override
    public int getId() {
        return 0x08;
    }

}
