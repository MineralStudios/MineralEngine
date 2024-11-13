package gg.mineral.server.command.impl;

import dev.zerite.craftlib.chat.type.ChatColor;
import gg.mineral.server.command.Command;
import gg.mineral.server.command.CommandExecutor;

public class VersionCommand extends Command {

    public VersionCommand() {
        super("", "version");
    }

    @Override
    public void execute(CommandExecutor commandExecutor, String[] arguments) {
        commandExecutor.msg(
                ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "-------------------------------");
        commandExecutor.msg("Mineral Engine" + ChatColor.GRAY.toString()
                + " (v1.0.0)");
        commandExecutor.msg(" ");
        commandExecutor.msg("Programming Language: " + ChatColor.AQUA.toString() + "Java");
        commandExecutor.msg("Developer: " + ChatColor.AQUA.toString() + "Jaiden");
        commandExecutor.msg(
                ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "-------------------------------");
    }

}
