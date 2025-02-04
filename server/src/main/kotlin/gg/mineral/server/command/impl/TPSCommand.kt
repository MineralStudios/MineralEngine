package gg.mineral.server.command.impl

import com.sun.management.OperatingSystemMXBean
import gg.mineral.api.command.Command
import gg.mineral.api.command.CommandExecutor
import gg.mineral.api.tick.TickLoop
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import java.lang.management.ManagementFactory
import kotlin.math.min

// TODO: add number of players, cached chunks etc....
class TPSCommand : Command("tps", "") {
    override fun execute(commandExecutor: CommandExecutor, arguments: Array<String?>) {


        commandExecutor.msg(
            ChatColor.UNDERLINE.toString() + "Performance" + ChatColor.GRAY.toString()
                    + " (CPU Threads)"
        )
        commandExecutor.msg(
            ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "-------------------------------"
        )

        commandExecutor.msg(" ")

        val components = mutableListOf<BaseComponent>()

        for (snapshot in commandExecutor.serverSnapshot.server.snapshots) {
            if (snapshot is TickLoop) {
                val hover = ComponentBuilder(
                    ChatColor.UNDERLINE.toString() + "Performance" + ChatColor.GRAY.toString()
                            + " (1m, 5m, 15m) [" + snapshot.name + "]\n\n" +
                            ChatColor.WHITE.toString() + "TPS: "
                            + getTpsStr(snapshot.tps1.average) + ChatColor.WHITE.toString() + ", "
                            + getTpsStr(snapshot.tps5.average) + ChatColor.WHITE.toString() + ", "
                            + getTpsStr(snapshot.tps15.average)
                ).create()

                val color = color(snapshot.tps1.average)

                val textComponent = TextComponent("$color\u2B1B")
                textComponent.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, hover)
                components.add(textComponent)
            }
        }

        val array = components.toTypedArray()

        val rowList = mutableListOf<BaseComponent>()

        for (i in array.indices) {
            rowList.add(TextComponent("  "))
            rowList.add(array[i])
            if (i % 8 == 7) {
                commandExecutor.msg(*rowList.toTypedArray())
                rowList.clear()
                commandExecutor.msg(" ")
            }
        }
        
        val osBean = ManagementFactory.getPlatformMXBean(
            OperatingSystemMXBean::class.java
        )
        commandExecutor.msg(ChatColor.UNDERLINE.toString() + "System Information")
        commandExecutor.msg(" ")
        commandExecutor.msg(
            (ChatColor.WHITE.toString() + "Operating System: " + ChatColor.AQUA.toString()
                    + getOSName(osBean))
        )
        commandExecutor.msg(
            (ChatColor.WHITE.toString() + "CPU Usage: " + ChatColor.AQUA.toString()
                    + getCpuUsage(osBean) + " %")
        )
        commandExecutor.msg(
            (ChatColor.WHITE.toString() + "CPU Architecture: " + ChatColor.AQUA.toString()
                    + getCpuArch(osBean))
        )
        commandExecutor.msg(
            (ChatColor.WHITE.toString() + "JVM Memory Usage: " + ChatColor.AQUA.toString()
                    + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576) + " MBs")
        )
        commandExecutor.msg(
            (ChatColor.WHITE.toString() + "JVM Total Memory: " + ChatColor.AQUA.toString()
                    + (Runtime.getRuntime().maxMemory() / 1048576) + " MBs")
        )

        commandExecutor.msg(
            ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "-------------------------------"
        )
    }

    private fun getCpuUsage(osBean: OperatingSystemMXBean): Double {
        val cpuLoad = osBean.systemCpuLoad * 100
        return round(cpuLoad)
    }

    private fun getCpuArch(osBean: OperatingSystemMXBean): String = osBean.arch

    private fun getOSName(osBean: OperatingSystemMXBean): String =
        osBean.name

    private fun getTpsStr(tps: Double): String {
        val color = color(tps)
        val roundTPS = round(tps)
        return color.toString() + min(20.0, roundTPS) + (if (roundTPS > 20) "*" else "")
    }

    companion object {
        private fun round(tps: Double): Double =
            Math.round(tps * 100.0) / 100.0

        private fun color(tps: Double): ChatColor =
            (if (tps > 18.0) ChatColor.GREEN else if (tps > 16.0) ChatColor.YELLOW else ChatColor.RED)
    }
}
