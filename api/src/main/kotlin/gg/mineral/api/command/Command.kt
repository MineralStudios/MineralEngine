package gg.mineral.api.command


abstract class Command(val name: String, val permission: String) {

    /**
     * Executes the command.
     *
     * @param commandExecutor The command executor.
     * @param arguments       The arguments.
     */
    abstract fun execute(commandExecutor: CommandExecutor, arguments: Array<String?>)
}
