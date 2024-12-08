package gg.mineral.server.network.packet.status.serverbound;

import dev.zerite.craftlib.chat.type.ChatColor;
import gg.mineral.api.network.connection.Connection;
import gg.mineral.api.network.packet.Packet;
import gg.mineral.server.network.packet.status.clientbound.ResponsePacket;
import gg.mineral.server.network.ping.ServerPing;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.val;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public final class RequestPacket implements Packet.INCOMING {

    @Override
    public void received(Connection connection) {

        val serverPing = new ServerPing(ChatColor.BLUE + "Custom Minecraft Server Software",
                (int) connection.getServer().getOnlinePlayers().size(),
                2024, 5,
                "Mineral", new ServerPing.Icon("server-icon.png"));
        connection.queuePacket(new ResponsePacket(serverPing.toJsonString()));
    }

    @Override
    public void deserialize(ByteBuf is) {
    }
}
