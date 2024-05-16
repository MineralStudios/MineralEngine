package gg.mineral.server.network.packet.play.serverbound;

import java.util.Arrays;

import dev.zerite.craftlib.chat.type.ChatColor;
import gg.mineral.server.command.Command;
import gg.mineral.server.command.CommandExecutor;
import gg.mineral.server.entity.manager.EntityManager;
import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public class ChatMessagePacket implements Packet.INCOMING {

    String message;

    @Override
    public void received(Connection connection) {
        if (message.startsWith("/")) {
            EntityManager.get(connection).ifPresent(player -> {
                CommandExecutor executor = player;
                String[] splitCommand = message.split(" ");
                String[] args = splitCommand.length > 1 ? Arrays.copyOfRange(splitCommand, 1, splitCommand.length)
                        : new String[0];
                Command.LIST.stream().filter(command -> splitCommand[0].equalsIgnoreCase("/" + command.getName()))
                        .findFirst()
                        .ifPresent(command -> command.execute(executor, args));
            });
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
