package gg.mineral.server.command;

import java.util.List;

import gg.mineral.server.util.GlueList;

public abstract class Command {
    public static List<Command> LIST = new GlueList<Command>();
    String permission, name;
    ArgumentType[] arguments;

    public Command(String permission, String name, ArgumentType[] arguments) {
        this.permission = permission;
        this.name = name;
        this.arguments = arguments;
    }

    public abstract void execute(CommandExecutor commandExecutor, String[] arguments);

    public String getName() {
        return name;
    }

    public static Command byName(String name) {
        for (Command command : LIST) {
            if (!command.getName().equalsIgnoreCase(name)) {
                continue;
            }

            return command;
        }

        return null;
    }
}
