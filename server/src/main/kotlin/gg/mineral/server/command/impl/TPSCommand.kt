package gg.mineral.server.command.impl

import com.sun.management.OperatingSystemMXBean
import dev.zerite.craftlib.chat.type.ChatColor
import gg.mineral.api.command.Command
import gg.mineral.api.command.CommandExecutor
import java.lang.management.ManagementFactory
import kotlin.math.min

// TODO: add number of players, cached chunks etc....
class TPSCommand : Command("tps", "") {
    override fun execute(commandExecutor: CommandExecutor, arguments: Array<String?>) {
        commandExecutor.msg(
            ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "-------------------------------"
        )
        val tickLoop = commandExecutor.server.tickLoop
        commandExecutor.msg(
            (ChatColor.UNDERLINE.toString() + "Performance" + ChatColor.GRAY.toString()
                    + " (1m, 5m, 15m)")
        )
        commandExecutor.msg(" ")
        commandExecutor
            .msg(
                (ChatColor.WHITE.toString() + "TPS: "
                        + getTpsStr(tickLoop.tps1.average) + ChatColor.WHITE.toString() + ", "
                        + getTpsStr(tickLoop.tps5.average) + ChatColor.WHITE.toString() + ", "
                        + getTpsStr(tickLoop.tps15.average))
            )

        commandExecutor.msg(" ")
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
