package gg.mineral.server.network.packet.play.serverbound;

import java.util.Arrays;

import dev.zerite.craftlib.chat.type.ChatColor;
import gg.mineral.server.command.Command;
import gg.mineral.server.entity.manager.EntityManager;
import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;
import lombok.val;

public class ChatMessagePacket implements Packet.INCOMING {

    String message;

    @Override
    public void received(Connection connection) {
        if (message.startsWith("/")) {
            val player = EntityManager.get(connection);
            if (player == null)
                return;

            val executor = player;
            val splitCommand = message.split(" ");
            val args = splitCommand.length > 1 ? Arrays.copyOfRange(splitCommand, 1, splitCommand.length)
                    : new String[0];
            Command.LIST.stream().filter(command -> splitCommand[0].equalsIgnoreCase("/" + command.getName()))
                    .findFirst()
                    .ifPresent(command -> command.execute(executor, args));
            return;
        }

        EntityManager.iteratePlayers(
                player -> player.msg(ChatColor.GREEN + connection.getName() + ChatColor.RESET + ": " + message));
    }

    @Override
    public void deserialize(ByteBuf is) {
        message = ByteBufUtil.readString(is);
    }
}
