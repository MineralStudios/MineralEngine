package gg.mineral.api.command

interface CommandMap : MutableMap<String?, Command?> {
    /**
     * Register a command
     *
     * @param command
     *
     * @return true if the command was registered, false if the command was already
     * registered
     */
    fun register(command: Command?): Boolean

    /**
     * Register multiple commands
     *
     * @param commands
     *
     * @return true if all commands were registered, false if any command was
     * already registered
     */
    fun registerAll(commands: Collection<Command?>?): Boolean

    /**
     * Unregister a command
     *
     * @param command
     *
     * @return true if the command was unregistered, false if the command was not
     */
    fun unregister(command: Command?): Boolean
}
