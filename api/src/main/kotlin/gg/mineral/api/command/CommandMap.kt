package gg.mineral.api.command

interface CommandMap {
    /**
     * Register a command
     *
     * @param command
     *
     * @return true if the command was registered, false if the command was already
     * registered
     */
    fun register(command: Command): Boolean

    /**
     * Register multiple commands
     *
     * @param commands
     *
     * @return true if all commands were registered, false if any command was
     * already registered
     */
    fun registerAll(commands: Collection<Command>): Boolean

    /**
     * Unregister a command
     *
     * @param command
     *
     * @return true if the command was unregistered, false if the command was not
     */
    fun unregister(command: Command): Boolean

    /**
     * Get a command by name
     *
     * @param name
     *
     * @return the command, or null if the command does not exist
     */
    fun get(name: String): Command?
}
