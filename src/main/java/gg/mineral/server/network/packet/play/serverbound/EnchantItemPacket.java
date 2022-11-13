package gg.mineral.server.network.packet.play.serverbound;

import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public class EnchantItemPacket implements Packet.INCOMING {
    byte windowId, enchantment;

    @Override
    public void received(Connection connection) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deserialize(ByteBuf is) {
        windowId = is.readByte();
        enchantment = is.readByte();
    }

    @Override
    public int getId() {
        return 0x11;
    }

}
