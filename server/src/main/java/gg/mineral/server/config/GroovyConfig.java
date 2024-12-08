package gg.mineral.server.config;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import dev.zerite.craftlib.chat.component.BaseChatComponent;
import dev.zerite.craftlib.chat.component.StringChatComponent;
import dev.zerite.craftlib.chat.type.ChatColor;
import groovy.util.ConfigObject;
import groovy.util.ConfigSlurper;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.val;

@Data
public class GroovyConfig {
    @Setting("server")
    private String brandName = "Mineral";
    @Setting("server")
    private String motd = ChatColor.BLUE + "Custom Minecraft Server Software";
    @Setting("server")
    private int maxPlayers = 2024;
    @Setting("server")
    private List<String> whitelistedPlayers = Collections.emptyList();
    @Setting("server")
    private String whitelistMessage = ChatColor.RED + "You are not whitelisted on this server.";
    @Setting("server")
    private String serverFullMessage = ChatColor.RED + "The server is full.";
    @Setting("server")
    private String outdatedClientMessage = ChatColor.RED + "Outdated client.";
    @Setting("server")
    private String outdatedServerMessage = ChatColor.RED + "Outdated server.";
    @Setting("server")
    private String restartMessage = ChatColor.RED + "Server is restarting.";
    @Setting("server")
    private String shutdownMessage = ChatColor.RED + "Server closed.";
    @Setting("server")
    private final BaseChatComponent disconnectUnknown = new StringChatComponent(
            ChatColor.RED + "You have been disconnected from the server.");
    @Setting("server")
    private final BaseChatComponent disconnectCanNotAuthenticate = new StringChatComponent(
            ChatColor.RED + "Your account was unable to be authenticated.");
    @Setting("server")
    private final BaseChatComponent disconnectAlreadyLoggedIn = new StringChatComponent(
            ChatColor.RED + "Your account was already logged in.");
    @Setting("player")
    private int viewDistance = 10;
    @Setting("player")
    private int relativeMoveFrequency = 2;
    @Setting("player.hunger")
    private float blockBreakExhaustion = 0.025f;
    @Setting("player.hunger")
    private float swimmingExhaustion = 0.015f;
    @Setting("player.hunger")
    private float walkExhaustion = 0.2f;
    @Setting("player.hunger")
    private float sprintExhaustion = 0.8f;
    @Setting("player.hunger")
    private float combatExhaustion = 0.3f;
    @Setting("player.hunger")
    private float regenExhaustion = 3.0f;
    @Setting("combat")
    private float playerBlockingDamageMultiplier = 0.5f;
    @Setting("combat")
    private boolean disablePlayerCrits = false;
    @Setting("combat.backtrack")
    private boolean backtrackRandom = false;
    @Setting("combat.backtrack")
    private boolean backtrackEnabled = false;
    @Setting("combat.backtrack")
    private boolean comboMode = false;
    @Setting("combat.backtrack")
    private double delayDistanceMin; // in blocks
    @Setting("combat.backtrack")
    private double delayDistanceMax; // in blocks
    @Setting("combat.backtrack")
    private int delayFactor; // in milliseconds
    @Setting("combat.backtrack")
    private int decayFactor; // in milliseconds
    @Setting("combat.backtrack")
    private int maxDelayMs; // in milliseconds
    @Setting("combat.backtrack")
    private int delayResetTime; // in ticks (20 ticks per second)
    @Setting("combat.backtrack")
    private int rMin, rMax;
    @Setting("entity")
    private boolean disableExplosionKnockback = false;
    @Setting("entity")
    private boolean disablePearlKnockback = false;
    @Setting("connection")
    private boolean onlineMode = true;
    @Setting("connection")
    private boolean proxy = false;// TODO: proxy
    @Setting("connection")
    private int port = 25565;
    @Setting("connection")
    private int networkCompressionThreshold = 256; // TODO: network compression
    @Setting("connection.packet-limiter") // TODO: packet limiter
    private String kickMessage = "Too many packets sent!";
    @Setting("plugin")
    private List<String> hiddenPlugins = Arrays.asList("SpookyAC");
    @Setting("plugin")
    private String pluginsFolder = "plugins";
    @Setting("world")
    private String worldsFolder = "worlds";

    private final File configFile = new File("config.groovy");

    @SneakyThrows
    public void load() {
        val configSlurper = new ConfigSlurper();

        if (!configFile.exists())
            configFile.createNewFile();

        val config = configSlurper.parse(configFile.toURI().toURL());

        for (val field : this.getClass().getDeclaredFields()) {
            val setting = field.getDeclaredAnnotation(Setting.class);
            if (setting == null)
                continue;

            val keys = setting.value().split("\\.");
            ConfigObject subConfig = null, lastSubConfig = config;

            for (val key : keys) {
                val nextObject = (ConfigObject) lastSubConfig.get(key);

                if (nextObject == null)
                    break;

                subConfig = nextObject;
                lastSubConfig = subConfig;
            }

            if (subConfig == null)
                continue;

            val value = subConfig.get(field.getName());

            if (value == null)
                continue;

            field.set(this, value);
        }
    }
}