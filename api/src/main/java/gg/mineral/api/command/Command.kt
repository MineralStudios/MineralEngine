package gg.mineral.api.command

import lombok.Data
import lombok.RequiredArgsConstructor

@RequiredArgsConstructor
@Data
abstract class Command {
    private val permission: String? = null
    private val name: String? = null

    /**
     * Executes the command.
     *
     * @param commandExecutor The command executor.
     * @param arguments       The arguments.
     */
    abstract fun execute(commandExecutor: CommandExecutor?, arguments: Array<String?>?)
}
