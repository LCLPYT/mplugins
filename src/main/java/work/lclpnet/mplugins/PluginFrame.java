package work.lclpnet.mplugins;

import org.slf4j.Logger;
import work.lclpnet.mplugins.ext.lib.FabricJsonManifestLoader;
import work.lclpnet.mplugins.ext.lib.FabricPluginContainer;
import work.lclpnet.plugin.PluginManager;
import work.lclpnet.plugin.SimplePluginManager;
import work.lclpnet.plugin.bootstrap.OrderedPluginBootstrap;
import work.lclpnet.plugin.discover.DirectoryPluginDiscoveryService;
import work.lclpnet.plugin.discover.PluginDiscoveryService;
import work.lclpnet.plugin.load.DefaultClassLoaderContainer;

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
    @Nullable
    private PluginDiscoveryService discoveryService = null;

    public PluginFrame(Path pluginDirectory, Logger logger) {
        this.pluginDirectory = pluginDirectory;
        this.logger = logger;
    }

    public void init() {
        ensurePluginDirectoryExists();

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, "Plugin Frame Shutdown"));

        clContainer = new DefaultClassLoaderContainer();

        discoveryService = new DirectoryPluginDiscoveryService(
                pluginDirectory, new FabricJsonManifestLoader(), clContainer, logger
        );
        var container = new FabricPluginContainer(logger);
        var bootstrap = new OrderedPluginBootstrap(discoveryService, container);

        pluginManager = new SimplePluginManager(discoveryService, container);

        try {
            bootstrap.loadPlugins();
        } catch (IOException e) {
            throw new RuntimeException("Plugin bootstrap failed", e);
        }

    }

    private void ensurePluginDirectoryExists() {
        if (Files.exists(pluginDirectory)) return;

        try {
            Files.createDirectories(pluginDirectory);
        } catch (IOException e) {
            throw new RuntimeException("Could not create plugin directory", e);
        }
    }

    public Path getPluginDirectory() {
        return pluginDirectory;
    }

    public Logger getLogger() {
        return logger;
    }

    public PluginManager getPluginManager() {
        if (pluginManager == null) notInitialized();

        return pluginManager;
    }

    public PluginDiscoveryService getDiscoveryService() {
        if (discoveryService == null) notInitialized();

        return discoveryService;
    }

    private void shutdown() {
        if (clContainer != null) clContainer.close();
    }

    private static void notInitialized() {
        throw new IllegalStateException("Plugin Frame is not initialized");
    }
}
