package gg.mineral.api.command;

import java.util.Collection;
import java.util.Map;

public interface CommandMap extends Map<String, Command> {
    /**
     * Register a command
     * 
     * @param command
     * 
     * @return true if the command was registered, false if the command was already
     *         registered
     */
    boolean register(Command command);

    /**
     * Register multiple commands
     * 
     * @param commands
     * 
     * @return true if all commands were registered, false if any command was
     *         already registered
     */
    boolean registerAll(Collection<? extends Command> commands);

    /**
     * Unregister a command
     * 
     * @param command
     * 
     * @return true if the command was unregistered, false if the command was not
     */
    boolean unregister(Command command);
}
