package gg.mineral.server.network.packet.play.serverbound;

import java.util.Arrays;

import dev.zerite.craftlib.chat.type.ChatColor;
import gg.mineral.api.network.connection.Connection;
import gg.mineral.api.network.packet.Packet;
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
public final class ChatMessagePacket implements Packet.INCOMING {
    private String message;

    @Override
    public void received(Connection connection) {
        val server = connection.getServer();
        if (message.startsWith("/")) {
            val player = connection.getPlayer();
            if (player == null)
                return;

            val splitCommand = message.split(" ");
            val args = splitCommand.length > 1 ? Arrays.copyOfRange(splitCommand, 1, splitCommand.length)
                    : new String[0];

            val commandName = splitCommand[0].substring(1);

            val commandMap = server.getRegisteredCommands();

            val command = commandMap.get(commandName);

            if (command != null)
                command.execute(player, args);
            return;
        }

        server.getOnlinePlayers().forEach(
                player -> player.msg(ChatColor.GREEN + connection.getName() + ChatColor.RESET + ": " + message));
    }

    @Override
    public void deserialize(ByteBuf is) {
        message = readString(is);
    }
}
