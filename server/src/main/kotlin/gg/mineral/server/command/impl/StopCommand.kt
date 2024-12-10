package gg.mineral.server.command.impl

import dev.zerite.craftlib.chat.type.ChatColor
import gg.mineral.api.command.Command
import gg.mineral.api.command.CommandExecutor

class StopCommand : Command("stop", "") {
    override fun execute(commandExecutor: CommandExecutor, arguments: Array<String?>) {
        commandExecutor.msg(ChatColor.RED.toString() + "Stopping server...")
        commandExecutor.server.shutdown()
    }
}
