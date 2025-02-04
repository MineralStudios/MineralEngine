package gg.mineral.server.command.impl

import gg.mineral.api.command.Command
import gg.mineral.api.command.CommandExecutor
import gg.mineral.api.entity.living.human.Player
import net.md_5.bungee.api.ChatColor

// TODO: add number of players, cached chunks etc....
class PingCommand : Command("ping", "") {
    override fun execute(commandExecutor: CommandExecutor, arguments: Array<String?>) {
        if (commandExecutor is Player) {
            commandExecutor.msg(
                ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "-------------------------------"
            )
            commandExecutor.msg(
                (ChatColor.UNDERLINE.toString() + "Connection Performance" + ChatColor.GRAY.toString())
            )
            commandExecutor.msg(" ")
            commandExecutor
                .msg(
                    (ChatColor.WHITE.toString() + "Latency: "
                            + getPingStr(commandExecutor.connection.ping))
                )

            commandExecutor.msg(" ")
            commandExecutor.msg(ChatColor.UNDERLINE.toString() + "Connection Information")
            commandExecutor.msg(" ")
            commandExecutor.msg(
                (ChatColor.WHITE.toString() + "Ip Address: " + ChatColor.AQUA.toString()
                        + commandExecutor.connection.ipAddress)
            )

            commandExecutor.msg(
                ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "-------------------------------"
            )
        }
    }

    private fun getPingStr(ping: Int): String {
        val color = color(ping)
        return color.toString() + ping + "ms"
    }

    companion object {
        private fun color(ping: Int): ChatColor =
            (if (ping < 75) ChatColor.GREEN else if (ping < 150) ChatColor.YELLOW else ChatColor.RED)
    }
}
