package gg.mineral.server.command.impl

import gg.mineral.api.command.Command
import gg.mineral.api.command.CommandExecutor
import net.md_5.bungee.api.ChatColor

class VersionCommand : Command("version", "") {
    override fun execute(commandExecutor: CommandExecutor, arguments: Array<String?>) {
        commandExecutor.msg(
            ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "-------------------------------"
        )
        commandExecutor.msg(
            ("Mineral Engine" + ChatColor.GRAY.toString()
                    + " (v1.0.0)")
        )
        commandExecutor.msg(" ")
        commandExecutor.msg("Programming Language: " + ChatColor.AQUA.toString() + "Kotlin")
        commandExecutor.msg("Developer: " + ChatColor.AQUA.toString() + "Jaiden")
        commandExecutor.msg(
            ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "-------------------------------"
        )
    }
}
