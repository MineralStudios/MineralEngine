package gg.mineral.server.network.packet.play.serverbound;

import java.util.Arrays;

import dev.zerite.craftlib.chat.type.ChatColor;
import gg.mineral.api.network.connection.Connection;
import gg.mineral.api.network.packet.Packet;
import gg.mineral.server.MinecraftServerImpl;
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
public final class ChatMessagePacket implements Packet.ASYNC_INCOMING {
    private String message;

    @Override
    public void receivedAsync(Connection connection) {
        if (message.startsWith("/")) // TODO: async command support
            return;
        val server = connection.getServer();
        val chatMsg = ChatColor.GREEN + connection.getName() + ChatColor.RESET + ": " + message;
        if (server instanceof MinecraftServerImpl impl)
            impl.msg(chatMsg);
        server.getOnlinePlayers().forEach(
                player -> player.msg(chatMsg));
    }

    @Override
    public void received(Connection connection) {

        if (!message.startsWith("/"))
            return;

        val server = connection.getServer();

        val player = connection.getPlayer();
        if (player == null)
            return;

        val splitCommand = message.split(" ");
        val args = splitCommand.length > 1 ? Arrays.copyOfRange(splitCommand, 1, splitCommand.length)
                : new String[0];

        val commandName = splitCommand[0].substring(1);

        val commandMap = server.getRegisteredCommands();

        val command = commandMap.get(commandName);

        if (command != null) {
            if (server instanceof MinecraftServerImpl impl)
                impl.msg(connection.getName() + " executed command: " + message);
            command.execute(player, args);
        }
    }

    @Override
    public void deserialize(ByteBuf is) {
        message = readString(is);
    }
}
