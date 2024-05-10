package gg.mineral.server.network.packet.play.serverbound;

import dev.zerite.craftlib.chat.type.ChatColor;
import gg.mineral.server.entity.manager.EntityManager;
import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public class ChatMessagePacket implements Packet.INCOMING {

    String message;

    @Override
    public void received(Connection connection) {
        EntityManager.iteratePlayers(player -> {
            player.msg(ChatColor.GREEN + connection.getName() + ChatColor.RESET + ": " + message);
        });
    }

    @Override
    public void deserialize(ByteBuf is) {
        message = ByteBufUtil.readString(is);
    }
}
