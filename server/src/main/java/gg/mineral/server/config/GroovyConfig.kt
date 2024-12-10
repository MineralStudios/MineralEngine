package gg.mineral.server.config

import dev.zerite.craftlib.chat.component.BaseChatComponent
import dev.zerite.craftlib.chat.component.StringChatComponent
import dev.zerite.craftlib.chat.type.ChatColor
import groovy.util.ConfigObject
import groovy.util.ConfigSlurper
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import lombok.Data
import lombok.SneakyThrows
import java.io.File

@Data
class GroovyConfig {
    @Setting("server")
    val disconnectUnknown: BaseChatComponent = StringChatComponent(
        ChatColor.RED.toString() + "You have been disconnected from the server."
    )
    val configFile: File = File("config.groovy")

    @Setting("server")
    private val disconnectCanNotAuthenticate: BaseChatComponent = StringChatComponent(
        ChatColor.RED.toString() + "Your account was unable to be authenticated."
    )

    @Setting("server")
    private val disconnectAlreadyLoggedIn: BaseChatComponent = StringChatComponent(
        ChatColor.RED.toString() + "Your account was already logged in."
    )

    @Setting("plugin")
    var pluginsFolder: String = "plugins"

    @Setting("connection")
    var port: Int = 25565

    @Setting("world")
    var worldsFolder: String = "worlds"

    @Setting("server")
    private val brandName = "Mineral"

    @Setting("server")
    private val motd = ChatColor.BLUE.toString() + "Custom Minecraft Server Software"

    @Setting("server")
    private val maxPlayers = 2024

    @Setting("server")
    private val whitelistedPlayers = emptyList<String>()

    @Setting("server")
    private val whitelistMessage = ChatColor.RED.toString() + "You are not whitelisted on this server."

    @Setting("server")
    private val serverFullMessage = ChatColor.RED.toString() + "The server is full."

    @Setting("server")
    private val outdatedClientMessage = ChatColor.RED.toString() + "Outdated client."

    @Setting("server")
    private val outdatedServerMessage = ChatColor.RED.toString() + "Outdated server."

    @Setting("server")
    private val restartMessage = ChatColor.RED.toString() + "Server is restarting."

    @Setting("server")
    private val shutdownMessage = ChatColor.RED.toString() + "Server closed."

    @Setting("player")
    private val viewDistance = 10

    @Setting("player")
    private val relativeMoveFrequency = 2

    @Setting("player.hunger")
    private val blockBreakExhaustion = 0.025f

    @Setting("player.hunger")
    private val swimmingExhaustion = 0.015f

    @Setting("player.hunger")
    private val walkExhaustion = 0.2f

    @Setting("player.hunger")
    private val sprintExhaustion = 0.8f

    @Setting("player.hunger")
    private val combatExhaustion = 0.3f

    @Setting("player.hunger")
    private val regenExhaustion = 3.0f

    @Setting("combat")
    private val playerBlockingDamageMultiplier = 0.5f

    @Setting("combat")
    private val disablePlayerCrits = false

    @Setting("combat.backtrack")
    private val backtrackRandom = false

    @Setting("combat.backtrack")
    private val backtrackEnabled = false

    @Setting("combat.backtrack")
    private val comboMode = false

    @Setting("combat.backtrack")
    private val delayDistanceMin = 0.0 // in blocks

    @Setting("combat.backtrack")
    private val delayDistanceMax = 0.0 // in blocks

    @Setting("combat.backtrack")
    private val delayFactor = 0 // in milliseconds

    @Setting("combat.backtrack")
    private val decayFactor = 0 // in milliseconds

    @Setting("combat.backtrack")
    private val maxDelayMs = 0 // in milliseconds

    @Setting("combat.backtrack")
    private val delayResetTime = 0 // in ticks (20 ticks per second)

    @Setting("combat.backtrack")
    private val rMin = 0

    @Setting("combat.backtrack")
    private val rMax = 0

    @Setting("entity")
    private val disableExplosionKnockback = false

    @Setting("entity")
    private val disablePearlKnockback = false

    @Setting("connection")
    private val onlineMode = true

    @Setting("connection")
    private val proxy = false // TODO: proxy

    @Setting("connection")
    private val networkCompressionThreshold = 256 // TODO: network compression

    @Setting("connection.packet-limiter") // TODO: packet limiter
    private val kickMessage = "Too many packets sent!"

    @Setting("plugin")
    private val hiddenPlugins = listOf("SpookyAC")

    @SneakyThrows
    fun load() {
        val configSlurper = ConfigSlurper()

        if (!configFile.exists()) configFile.createNewFile()

        val config = configSlurper.parse(configFile.toURI().toURL())
        val configCache = Object2ObjectOpenHashMap<String, Any>()

        flattenConfig("", config, configCache)

        for (field in javaClass.declaredFields) {
            val setting = field.getDeclaredAnnotation(
                Setting::class.java
            )
            if (setting == null) continue

            val key = setting.value + "." + field.name
            val value = configCache[key] ?: continue

            field.isAccessible = true
            field[this] = value
        }
    }

    private fun flattenConfig(prefix: String, source: ConfigObject, target: Object2ObjectOpenHashMap<String, Any>) {
        source.forEach { (key: Any?, value: Any?) ->
            val newKey = if (prefix.isEmpty()) key.toString() else prefix + "." + key.toString()
            if (value is ConfigObject) flattenConfig(newKey, value, target)
            else target[newKey] = value
        }
    }
}
