package gg.mineral.server.network.packet.play.serverbound

import gg.mineral.api.command.Command
import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.Packet
import gg.mineral.server.MinecraftServerImpl
import io.netty.buffer.ByteBuf
import net.md_5.bungee.api.ChatColor
import java.util.*

class ChatMessagePacket(private var message: String? = null) : Packet.Incoming, Packet.AsyncHandler,
    Packet.SyncHandler {
    override suspend fun receivedAsync(connection: Connection) {
        if (message!!.startsWith("/"))  // TODO: async command support
            return
        val server = connection.serverSnapshot.server
        val chatMsg = ChatColor.GREEN.toString() + connection.name + ChatColor.RESET + ": " + message
        if (server is MinecraftServerImpl) server.msg(chatMsg)
        server.broadcastMessage(chatMsg)
    }

    override suspend fun receivedSync(connection: Connection) {
        if (message?.startsWith("/") == false) return

        val server = connection.serverSnapshot.server

        val player = connection.player ?: return

        val splitCommand = message!!.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val args = if (splitCommand.size > 1)
            Arrays.copyOfRange(splitCommand, 1, splitCommand.size)
        else
            arrayOfNulls(0)

        val commandName = splitCommand[0].substring(1)

        val commandMap = server.registeredCommands

        val command: Command? = commandMap.get(commandName)

        if (server is MinecraftServerImpl) server.msg(connection.name + " executed command: " + message)
        command?.execute(player, args)
    }

    override fun deserialize(`is`: ByteBuf) {
        message = `is`.readString()
    }
}
