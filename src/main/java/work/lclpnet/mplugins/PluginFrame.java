package work.lclpnet.mplugins;

import org.slf4j.Logger;
import work.lclpnet.plugin.DistinctPluginContainer;
import work.lclpnet.plugin.PluginManager;
import work.lclpnet.plugin.SimplePluginManager;
import work.lclpnet.plugin.bootstrap.OrderedPluginBootstrap;
import work.lclpnet.plugin.discover.DirectoryPluginDiscoveryService;
import work.lclpnet.plugin.load.DefaultClassLoaderContainer;
import work.lclpnet.plugin.manifest.JsonManifestLoader;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PluginFrame {

    private final Path pluginDirectory;
    private final Logger logger;
    @Nullable
    private DefaultClassLoaderContainer clContainer = null;
    @Nullable
    private PluginManager pluginManager = null;

    public PluginFrame(Path pluginDirectory, Logger logger) {
        this.pluginDirectory = pluginDirectory;
        this.logger = logger;
    }

    public void init() {
        ensurePluginDirectoryExists();

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, "Plugin Frame Shutdown"));

        clContainer = new DefaultClassLoaderContainer();

        var discovery = new DirectoryPluginDiscoveryService(
                pluginDirectory, new JsonManifestLoader(), clContainer, logger
        );
        var container = new DistinctPluginContainer(logger);
        var bootstrap = new OrderedPluginBootstrap(discovery, container);

        try {
            bootstrap.loadPlugins();
        } catch (IOException e) {
            throw new RuntimeException("Plugin bootstrap failed", e);
        }

        pluginManager = new SimplePluginManager(discovery, container);
    }

    private void ensurePluginDirectoryExists() {
        if (Files.exists(pluginDirectory)) return;

        try {
            Files.createDirectories(pluginDirectory);
        } catch (IOException e) {
            throw new RuntimeException("Could not create plugin directory", e);
        }
    }

    public PluginManager getPluginManager() {
        if (pluginManager == null)
            throw new IllegalStateException("Plugin Frame is not initialized");

        return pluginManager;
    }

    private void shutdown() {
        if (clContainer != null) clContainer.close();
    }
}
