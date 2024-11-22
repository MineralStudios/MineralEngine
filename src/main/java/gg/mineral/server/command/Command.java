package gg.mineral.server.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class Command {
    private final String permission, name;

    public abstract void execute(CommandExecutor commandExecutor, String[] arguments);
}
