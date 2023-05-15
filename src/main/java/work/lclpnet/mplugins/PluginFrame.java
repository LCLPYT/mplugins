package work.lclpnet.mplugins;

import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import work.lclpnet.mplugins.config.KibuDevConfig;
import work.lclpnet.mplugins.ext.lib.FabricJsonManifestLoader;
import work.lclpnet.mplugins.ext.lib.FabricPluginContainer;
import work.lclpnet.plugin.PluginManager;
import work.lclpnet.plugin.SimplePluginManager;
import work.lclpnet.plugin.bootstrap.OrderedPluginBootstrap;
import work.lclpnet.plugin.discover.ClasspathPluginDiscoveryService;
import work.lclpnet.plugin.discover.DirectoryPluginDiscoveryService;
import work.lclpnet.plugin.discover.MultiPluginDiscoveryService;
import work.lclpnet.plugin.discover.PluginDiscoveryService;
import work.lclpnet.plugin.load.DefaultClassLoaderContainer;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class PluginFrame {

    private final Options options;
    private final Logger logger;
    @Nullable
    private DefaultClassLoaderContainer clContainer = null;
    @Nullable
    private PluginManager pluginManager = null;
    @Nullable
    private PluginDiscoveryService discoveryService = null;

    public PluginFrame(Options options, Logger logger) {
        this.options = options;
        this.logger = logger;
    }

    public void init() {
        ensurePluginDirectoryExists();

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, "Plugin Frame Shutdown"));

        clContainer = new DefaultClassLoaderContainer();

        discoveryService = getPluginDiscoveryService();
        var container = new FabricPluginContainer(logger);
        var bootstrap = new OrderedPluginBootstrap(discoveryService, container);

        pluginManager = new SimplePluginManager(discoveryService, container);

        if (options.autoLoadPlugins) {
            try {
                bootstrap.loadPlugins();
            } catch (IOException e) {
                throw new RuntimeException("Plugin bootstrap failed", e);
            }
        }
    }

    private PluginDiscoveryService getPluginDiscoveryService() {
        final var manifestLoader = new FabricJsonManifestLoader();
        final var directoryLoader = new DirectoryPluginDiscoveryService(options.pluginDirectory(), manifestLoader, clContainer, logger);

        if (!FabricLoader.getInstance().isDevelopmentEnvironment()) return directoryLoader;

        KibuDevConfig kibuDevConfig = new KibuDevConfig(logger);
        kibuDevConfig.load();

        List<URL[]> classpath = kibuDevConfig.getPluginPaths();
        if (classpath == null) return directoryLoader;

        var devLoader = new ClasspathPluginDiscoveryService(classpath, manifestLoader, clContainer, logger);

        return new MultiPluginDiscoveryService(directoryLoader, devLoader);
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
