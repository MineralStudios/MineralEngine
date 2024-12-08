package gg.mineral.api.plugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gg.mineral.api.MinecraftServer;
import lombok.Cleanup;
import lombok.Getter;
import lombok.val;

public class PluginLoader {
    private static final Logger LOGGER = LogManager.getLogger(PluginLoader.class);
    private final MinecraftServer server;
    private final File pluginDirectory;
    @Getter
    private final List<MineralPlugin> loadedPlugins = new ArrayList<>();

    public PluginLoader(MinecraftServer server, File pluginDirectory) {
        this.server = server;
        this.pluginDirectory = pluginDirectory;

        if (!pluginDirectory.exists())
            pluginDirectory.mkdirs();
    }

    public void loadPlugins() throws IOException {
        val files = pluginDirectory.listFiles((dir, name) -> name.endsWith(".jar"));
        if (files == null || files.length == 0)
            return;

        for (val file : files) {
            try {
                loadPlugin(file);
            } catch (Exception e) {
                LOGGER.error("Failed to load plugin from " + file.getName());
                e.printStackTrace();
            }
        }
    }

    private static List<String> scanJarForClasses(File jarFile) throws IOException {
        val classNames = new ArrayList<String>();
        try (val jar = new JarFile(jarFile)) {
            val entries = jar.entries();
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement();
                if (entry.getName().endsWith(".class") && !entry.isDirectory()) {
                    val className = entry.getName().replace("/", ".").replace(".class", "");
                    classNames.add(className);
                }
            }
        }
        return classNames;
    }

    private void loadPlugin(File jarFile) throws Exception {
        @Cleanup
        val loader = new URLClassLoader(new URL[] { jarFile.toURI().toURL() }, this.getClass().getClassLoader());

        for (val className : scanJarForClasses(jarFile)) {
            val clazz = loader.loadClass(className);

            if (MineralPlugin.class.isAssignableFrom(clazz) && clazz.isAnnotationPresent(Plugin.class)) {
                val pluginAnnotation = clazz.getAnnotation(Plugin.class);
                LOGGER.info("Loading plugin " + pluginAnnotation.name() + " v" + pluginAnnotation.version());

                val constructor = clazz.getConstructor(MinecraftServer.class);
                val plugin = (MineralPlugin) constructor.newInstance(server);
                loadedPlugins.add(plugin);
                plugin.onEnable();
            }
        }
    }

    public void disablePlugins() {
        for (val plugin : loadedPlugins) {
            try {
                plugin.onDisable();
            } catch (Exception e) {
                LOGGER.error("Error disabling plugin " + plugin.getClass().getName());
                e.printStackTrace();
            }
        }
    }
}
