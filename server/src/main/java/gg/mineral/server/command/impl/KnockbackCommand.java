package gg.mineral.server.command.impl;

import gg.mineral.server.command.Command;
import gg.mineral.server.command.CommandExecutor;

public class KnockbackCommand extends Command {
    public KnockbackCommand() {
        super("", "knockback");
    }

    public static double x = 0.35, y = 0.35, z = 0.35, extraX = 0.425, extraY = 0.085, extraZ = 0.425, yLimit = 0.4,
            friction = 2;

    @Override
    public void execute(CommandExecutor commandExecutor, String[] arguments) {
        if (arguments.length == 0) {
            commandExecutor.msg("Usage: /knockback <x> <y> <z> <extraX> <extraY> <extraZ> <yLimit> <friction>");
            return;
        }

        try {
            x = Double.parseDouble(arguments[0]);
            y = Double.parseDouble(arguments[1]);
            z = Double.parseDouble(arguments[2]);
            extraX = Double.parseDouble(arguments[3]);
            extraY = Double.parseDouble(arguments[4]);
            extraZ = Double.parseDouble(arguments[5]);
            yLimit = Double.parseDouble(arguments[6]);
            friction = Double.parseDouble(arguments[7]);
        } catch (NumberFormatException e) {
            commandExecutor.msg("Invalid number format.");
            return;
        }

        commandExecutor.msg(
                "Knockback set to: " + x + " " + y + " " + z + " " + extraX + " " + extraY + " " + extraZ + " " + yLimit
                        + " " + friction);
    }

}
