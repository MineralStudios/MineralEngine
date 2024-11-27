package gg.mineral.api.command;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public abstract class Command {
    private final String permission, name;

    /**
     * Executes the command.
     * 
     * @param commandExecutor The command executor.
     * @param arguments       The arguments.
     */
    public abstract void execute(CommandExecutor commandExecutor, String[] arguments);
}
