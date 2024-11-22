package gg.mineral.server.network.packet.status.serverbound;

import dev.zerite.craftlib.chat.type.ChatColor;
import gg.mineral.server.entity.living.human.Player;
import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.network.packet.status.clientbound.ResponsePacket;
import gg.mineral.server.network.ping.ServerPing;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.val;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class RequestPacket implements Packet.INCOMING {

    @Override
    public void received(Connection connection) {

        val serverPing = new ServerPing(ChatColor.BLUE + "Custom Minecraft Server Software",
                (int) connection.getServer().getEntityManager().getEntities().values().stream()
                        .filter(e -> e instanceof Player).count(),
                2024, 5,
                "Mineral");
        connection.queuePacket(new ResponsePacket(serverPing.toJsonString()));
    }

    @Override
    public void deserialize(ByteBuf is) {
    }
}
