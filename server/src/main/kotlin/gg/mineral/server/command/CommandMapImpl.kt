package gg.mineral.server.command

import gg.mineral.api.command.Command
import gg.mineral.api.command.CommandMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap

open class CommandMapImpl : Object2ObjectOpenHashMap<String, Command>(), CommandMap {
    override fun register(command: Command): Boolean {
        if (containsKey(command.name)) return false

        put(command.name, command)
        return true
    }

    override fun unregister(command: Command): Boolean {
        return remove(command.name) != null
    }

    override fun registerAll(commands: Collection<Command>): Boolean {
        var success = true
        for (command in commands) success = success and register(command)
        return success
    }
}
