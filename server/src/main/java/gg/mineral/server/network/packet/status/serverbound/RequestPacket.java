package gg.mineral.server.network.packet.status.serverbound;

import gg.mineral.api.network.connection.Connection;
import gg.mineral.api.network.packet.Packet;
import gg.mineral.server.MinecraftServerImpl;
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

        if (connection.getServer() instanceof MinecraftServerImpl impl) {

            val config = impl.getConfig();

            val serverPing = new ServerPing(config.getMotd(),
                    (int) connection.getServer().getOnlinePlayers().size(),
                    config.getMaxPlayers(), 5,
                    config.getBrandName(), new ServerPing.Icon("server-icon.png"));
            connection.queuePacket(new ResponsePacket(serverPing.toJsonString()));
        }
    }

    @Override
    public void deserialize(ByteBuf is) {
    }
}
