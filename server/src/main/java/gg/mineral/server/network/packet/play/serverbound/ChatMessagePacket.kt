package gg.mineral.server.network.packet.play.serverbound

import dev.zerite.craftlib.chat.type.ChatColor
import gg.mineral.api.entity.living.human.Player
import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.Packet
import gg.mineral.server.MinecraftServerImpl
import io.netty.buffer.ByteBuf
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import lombok.experimental.Accessors
import java.util.*
import java.util.function.Consumer

@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(fluent = true)
class ChatMessagePacket : Packet.ASYNC_INCOMING {
    private var message: String? = null

    override fun receivedAsync(connection: Connection) {
        if (message!!.startsWith("/"))  // TODO: async command support
            return
        val server = connection.server
        val chatMsg = ChatColor.GREEN.toString() + connection.name + ChatColor.RESET + ": " + message
        if (server is MinecraftServerImpl) server.msg(chatMsg)
        server.onlinePlayers.forEach(
            Consumer { player: Player -> player.msg(chatMsg) })
    }

    override fun received(connection: Connection) {
        if (!message!!.startsWith("/")) return

        val server = connection.server

        val player = connection.player ?: return

        val splitCommand = message!!.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val args = if (splitCommand.size > 1)
            Arrays.copyOfRange(splitCommand, 1, splitCommand.size)
        else
            arrayOfNulls(0)

        val commandName = splitCommand[0].substring(1)

        val commandMap = server.registeredCommands

        val command: Any = commandMap.get(commandName)

        if (command != null) {
            if (server is MinecraftServerImpl) server.msg(connection.name + " executed command: " + message)
            command.execute(player, args)
        }
    }

    override fun deserialize(`is`: ByteBuf) {
        message = readString(`is`)
    }
}
