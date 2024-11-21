package gg.mineral.server.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class Command {
    @Getter
    private final String permission, name;

    public abstract void execute(CommandExecutor commandExecutor, String[] arguments);
}
