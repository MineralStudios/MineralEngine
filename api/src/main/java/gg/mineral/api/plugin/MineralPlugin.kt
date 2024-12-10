package gg.mineral.api.plugin

import gg.mineral.api.command.Command
import gg.mineral.api.plugin.listener.Listener
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import lombok.Getter
import lombok.SneakyThrows

@Getter
abstract class MineralPlugin {
    private val listeners: MutableList<Listener> = ArrayList()
    private val commands: MutableMap<String, Command> = Object2ObjectOpenHashMap()

    @SneakyThrows
    fun registerListener(vararg listenerClasses: Class<*>) {
        for (listenerClass in listenerClasses) {
            val classLoader = listenerClass.classLoader
            val generated = classLoader.loadClass(listenerClass.name + "_Generated").asSubclass(
                Listener::class.java
            )
            val listener = generated.getDeclaredConstructor().newInstance()
            listeners.add(listener)
        }
    }

    fun registerCommand(vararg commands: Command) {
        for (command in commands) this.commands[command.getName()] =
            command
    }

    fun hasCommand(name: String): Boolean {
        return commands.containsKey(name)
    }

    abstract fun onEnable()

    abstract fun onDisable()
}
