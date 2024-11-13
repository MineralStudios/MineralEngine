package gg.mineral.server.command;

import java.util.List;

import gg.mineral.server.command.impl.KnockbackCommand;
import gg.mineral.server.command.impl.TPSCommand;
import gg.mineral.server.command.impl.VersionCommand;
import gg.mineral.server.util.collection.GlueList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;

@AllArgsConstructor
public abstract class Command {
    public static List<Command> LIST = new GlueList<Command>();

    static {
        LIST.add(new TPSCommand());
        LIST.add(new VersionCommand());
        LIST.add(new KnockbackCommand());
    }

    @Getter
    String permission, name;

    public abstract void execute(CommandExecutor commandExecutor, String[] arguments);

    public static Command byName(String name) {
        for (val command : LIST)
            if (command.getName().equalsIgnoreCase(name))
                return command;

        return null;
    }
}
