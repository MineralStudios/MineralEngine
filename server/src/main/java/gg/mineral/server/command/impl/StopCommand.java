package gg.mineral.server.command.impl;

import dev.zerite.craftlib.chat.type.ChatColor;
import gg.mineral.api.command.Command;
import gg.mineral.api.command.CommandExecutor;

public class StopCommand extends Command {

    public StopCommand() {
        super("", "stop");
    }

    @Override
    public void execute(CommandExecutor commandExecutor, String[] arguments) {
        commandExecutor.msg(ChatColor.RED + "Stopping server...");
        commandExecutor.getServer().shutdown();
    }

}
