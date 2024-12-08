package gg.mineral.api.plugin;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.groovy.control.CompilationFailedException;
import org.eclipse.jdt.annotation.Nullable;

import gg.mineral.api.MinecraftServer;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
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
                LOGGER.error("Failed to load plugin from " + file.getName(), e);
            }
        }
    }

    private void loadPlugin(File jarFile) throws Exception {
        @Cleanup
        val loader = new URLClassLoader(new URL[] { jarFile.toURI().toURL() }, this.getClass().getClassLoader());

        val mainClass = loadMainClassFromGroovy(loader, jarFile);
        if (mainClass == null) {
            LOGGER.error("Failed to find main class in plugin " + jarFile.getName());
            return;
        }

        val clazz = loader.loadClass(mainClass);
        if (MineralPlugin.class.isAssignableFrom(clazz)) {
            val constructor = clazz.getConstructor(MinecraftServer.class);
            val plugin = (MineralPlugin) constructor.newInstance(server);

            if (plugin == null) {
                LOGGER.error("Failed to instantiate plugin " + clazz.getName());
                return;
            }

            loadedPlugins.add(plugin);
            plugin.onEnable();
        } else {
            LOGGER.error("Main class " + mainClass + " does not extend MineralPlugin");
        }
    }

    @Nullable
    private String loadMainClassFromGroovy(URLClassLoader loader, File jarFile)
            throws CompilationFailedException, IOException {
        val resource = loader.findResource("plugin.groovy");
        if (resource == null) {
            LOGGER.error("plugin.groovy not found in " + jarFile.getName());
            return null;
        }

        val binding = new Binding();
        val shell = new GroovyShell(loader, binding);

        try (val reader = new FileReader(new File(resource.toURI()))) {
            shell.evaluate(reader);
            return (String) binding.getVariable("mainClass");
        } catch (Exception e) {
            LOGGER.error("Failed to parse plugin.groovy in " + jarFile.getName(), e);
            return null;
        }
    }

    public void disablePlugins() {
        for (val plugin : loadedPlugins) {
            try {
                plugin.onDisable();
            } catch (Exception e) {
                LOGGER.error("Error disabling plugin " + plugin.getClass().getName(), e);
            }
        }
    }
}
