package gg.mineral.server.command.impl;

import java.lang.management.ManagementFactory;

import com.sun.management.OperatingSystemMXBean;

import dev.zerite.craftlib.chat.type.ChatColor;
import gg.mineral.api.command.Command;
import gg.mineral.api.command.CommandExecutor;
import lombok.val;

// TODO: add number of players, cached chunks etc....
public class TPSCommand extends Command {

    public TPSCommand() {
        super("", "tps");
    }

    @Override
    public void execute(CommandExecutor commandExecutor, String[] arguments) {
        commandExecutor.msg(
                ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "-------------------------------");
        val tickLoop = commandExecutor.getServer().getTickLoop();
        commandExecutor.msg(ChatColor.UNDERLINE.toString() + "Performance" + ChatColor.GRAY.toString()
                + " (1m, 5m, 15m)");
        commandExecutor.msg(" ");
        commandExecutor
                .msg(ChatColor.WHITE.toString() + "TPS: "
                        + getTpsStr(tickLoop.getTps1().getAverage()) + ChatColor.WHITE.toString() + ", "
                        + getTpsStr(tickLoop.getTps5().getAverage()) + ChatColor.WHITE.toString() + ", "
                        + getTpsStr(tickLoop.getTps15().getAverage()));

        commandExecutor.msg(" ");
        @SuppressWarnings("null")
        val osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
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
