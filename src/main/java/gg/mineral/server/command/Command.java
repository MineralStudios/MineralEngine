package gg.mineral.server.command;

import java.util.List;

import gg.mineral.server.util.collection.GlueList;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public abstract class Command {
    public static List<Command> LIST = new GlueList<Command>();
    @Getter
    String permission, name;
    ArgumentType[] arguments;

    public abstract void execute(CommandExecutor commandExecutor, String[] arguments);

    public static Command byName(String name) {
        for (Command command : LIST)
            if (command.getName().equalsIgnoreCase(name))
                return command;

        return null;
    }
}
