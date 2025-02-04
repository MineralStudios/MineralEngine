package gg.mineral.server.command.impl

import gg.mineral.api.command.Command
import gg.mineral.api.command.CommandExecutor
import net.md_5.bungee.api.ChatColor

class StopCommand : Command("stop", "") {
    override fun execute(commandExecutor: CommandExecutor, arguments: Array<String?>) {
        commandExecutor.msg(ChatColor.RED.toString() + "Stopping server...")
        commandExecutor.serverSnapshot.server.shutdown()
    }
}
