package gg.mineral.server.network.packet.play.serverbound;

import java.util.Arrays;

import dev.zerite.craftlib.chat.type.ChatColor;
import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;
import lombok.experimental.Accessors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(fluent = true)
public class ChatMessagePacket implements Packet.INCOMING {
    private String message;

    @Override
    public void received(Connection connection) {
        val entityManager = connection.getServer().getEntityManager();
        if (message.startsWith("/")) {
            val player = entityManager.get(connection);
            if (player == null)
                return;

            val splitCommand = message.split(" ");
            val args = splitCommand.length > 1 ? Arrays.copyOfRange(splitCommand, 1, splitCommand.length)
                    : new String[0];
            connection.getServer().getRegisteredCommands().stream()
                    .filter(command -> splitCommand[0].equalsIgnoreCase("/" + command.getName()))
                    .findFirst()
                    .ifPresent(command -> command.execute(player, args));
            return;
        }

        entityManager.iteratePlayers(
                player -> player.msg(ChatColor.GREEN + connection.getName() + ChatColor.RESET + ": " + message));
    }

    @Override
    public void deserialize(ByteBuf is) {
        message = ByteBufUtil.readString(is);
    }
}
