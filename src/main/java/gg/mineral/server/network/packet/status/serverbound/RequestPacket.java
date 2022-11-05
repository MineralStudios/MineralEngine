package gg.mineral.server.network.packet.status.serverbound;

import dev.zerite.craftlib.chat.type.ChatColor;
import gg.mineral.server.network.Connection;
import gg.mineral.server.network.packet.IncomingPacket;
import gg.mineral.server.network.packet.status.clientbound.ResponsePacket;
import gg.mineral.server.network.ping.ServerPing;
import io.netty.buffer.ByteBuf;

public class RequestPacket extends IncomingPacket {

    @Override
    public void received(Connection connection) {
        ServerPing serverPing = new ServerPing(ChatColor.BLUE + "Custom Minecraft Server Software", 0, 10, 5,
                "Mineral");
        connection.sendPacket(new ResponsePacket(serverPing));

    }

    @Override
    public void deserialize(ByteBuf is) {
    }

    @Override
    public int getId() {
        return 0x00;
    }

}
