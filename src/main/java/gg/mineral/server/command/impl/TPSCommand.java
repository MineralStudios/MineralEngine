package gg.mineral.server.command.impl;

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

import dev.zerite.craftlib.chat.type.ChatColor;
import gg.mineral.server.MinecraftServer;
import gg.mineral.server.command.Command;
import gg.mineral.server.command.CommandExecutor;
import gg.mineral.server.tick.TickLoop;

public class TPSCommand extends Command {

    public TPSCommand() {
        super("", "tps");
    }

    @Override
    public void execute(CommandExecutor commandExecutor, String[] arguments) {
        commandExecutor.msg(
                ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "-------------------------------");
        TickLoop tickLoop = MinecraftServer.getTickLoop();
        commandExecutor.msg(ChatColor.UNDERLINE.toString() + "Performance" + ChatColor.GRAY.toString()
                + " (1m, 5m, 15m)");
        commandExecutor.msg(" ");
        for (int t = 0; t < MinecraftServer.getTickThreadCount(); t++)
            commandExecutor
                    .msg(ChatColor.WHITE.toString() + "Thread " + t
                            + ": "
                            + getTpsStr(tickLoop.tps1[t].getAverage()) + ChatColor.WHITE.toString() + ", "
                            + getTpsStr(tickLoop.tps5[t].getAverage()) + ChatColor.WHITE.toString() + ", "
                            + getTpsStr(tickLoop.tps15[t].getAverage()));

        commandExecutor.msg(" ");
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        commandExecutor.msg(ChatColor.UNDERLINE.toString() + "System Information");
        commandExecutor.msg(" ");
        commandExecutor.msg(ChatColor.WHITE.toString() + "Operating System: " + ChatColor.AQUA.toString()
                + getOSName(osBean));
        commandExecutor.msg(ChatColor.WHITE.toString() + "CPU Usage: " + ChatColor.AQUA.toString()
                + getCpuUsage(osBean) + " %");
        commandExecutor.msg(ChatColor.WHITE.toString() + "CPU Architechure: " + ChatColor.AQUA.toString()
                + getCpuArch(osBean));
        commandExecutor.msg(ChatColor.WHITE.toString() + "JVM Memory Usage: " + ChatColor.AQUA.toString()
                + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576) + " MBs");
        commandExecutor.msg(ChatColor.WHITE.toString() + "JVM Total Memory: " + ChatColor.AQUA.toString()
                + (Runtime.getRuntime().maxMemory() / 1048576) + " MBs");

        commandExecutor.msg(
                ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "-------------------------------");
    }

    public double getCpuUsage(OperatingSystemMXBean osBean) {
        // Get the OperatingSystemMXBean instance

        // Get the system CPU load
        @SuppressWarnings("deprecation")
        double cpuLoad = osBean.getSystemCpuLoad() * 100;

        return round(cpuLoad);
    }

    public String getCpuArch(OperatingSystemMXBean osBean) {
        // Get the OperatingSystemMXBean instance

        return osBean.getArch();
    }

    public String getOSName(OperatingSystemMXBean osBean) {
        return osBean.getName();
    }

    public String getTpsStr(double tps) {
        ChatColor color = color(tps);
        double roundTPS = round(tps);
        return color.toString() + Math.min(20, roundTPS) + (roundTPS > 20 ? "*" : "");
    }

    private static double round(double tps) {
        return Math.round(tps * 100.0) / 100.0;
    }

    private static ChatColor color(double tps) {
        return ((tps > 18.0) ? ChatColor.GREEN : (tps > 16.0) ? ChatColor.YELLOW : ChatColor.RED);
    }

}
