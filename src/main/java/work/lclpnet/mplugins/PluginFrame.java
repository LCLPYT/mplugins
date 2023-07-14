package work.lclpnet.mplugins;

import org.slf4j.Logger;
import work.lclpnet.plugin.PluginManager;
import work.lclpnet.plugin.bootstrap.PluginBootstrap;
import work.lclpnet.plugin.discover.PluginDiscoveryService;
import work.lclpnet.plugin.load.DefaultClassLoaderContainer;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Singleton
public class PluginFrame {

    private final Options options;
    private final Logger logger;
    private final DefaultClassLoaderContainer clContainer;
    private final PluginDiscoveryService discoveryService;
    private final PluginBootstrap pluginBootstrap;
    private final PluginManager pluginManager;

    @Inject
    public PluginFrame(Options options, Logger logger, DefaultClassLoaderContainer clContainer,
                       PluginDiscoveryService discoveryService, PluginBootstrap pluginBootstrap,
                       PluginManager pluginManager) {
        this.options = options;
        this.logger = logger;
        this.clContainer = clContainer;
        this.discoveryService = discoveryService;
        this.pluginBootstrap = pluginBootstrap;
        this.pluginManager = pluginManager;
    }

    public void init() {
        ensurePluginDirectoryExists();

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, "Plugin Frame Shutdown"));

        if (!options.autoLoadPlugins) return;

        try {
            pluginBootstrap.loadPlugins();
        } catch (IOException e) {
            throw new RuntimeException("Plugin bootstrap failed", e);
        }
    }

    private void ensurePluginDirectoryExists() {
        if (Files.exists(options.pluginDirectory())) return;

        try {
            Files.createDirectories(options.pluginDirectory());
        } catch (IOException e) {
            throw new RuntimeException("Could not create plugin directory", e);
        }
    }

    public Path getPluginDirectory() {
        return options.pluginDirectory();
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

    public record Options(Path pluginDirectory, boolean autoLoadPlugins) {}
}
