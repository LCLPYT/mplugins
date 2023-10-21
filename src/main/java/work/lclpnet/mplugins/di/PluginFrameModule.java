package work.lclpnet.mplugins.di;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import work.lclpnet.mplugins.PluginFrame;
import work.lclpnet.mplugins.config.Config;
import work.lclpnet.mplugins.config.KibuDevConfig;
import work.lclpnet.mplugins.ext.lib.FabricJsonManifestLoader;
import work.lclpnet.mplugins.ext.lib.FabricPluginContainer;
import work.lclpnet.mplugins.ext.lib.FabricPluginManager;
import work.lclpnet.plugin.PluginContainer;
import work.lclpnet.plugin.PluginManager;
import work.lclpnet.plugin.bootstrap.OrderedPluginBootstrap;
import work.lclpnet.plugin.bootstrap.PluginBootstrap;
import work.lclpnet.plugin.discover.ClasspathPluginDiscoveryService;
import work.lclpnet.plugin.discover.DirectoryPluginDiscoveryService;
import work.lclpnet.plugin.discover.MultiPluginDiscoveryService;
import work.lclpnet.plugin.discover.PluginDiscoveryService;
import work.lclpnet.plugin.load.DefaultClassLoaderContainer;

import javax.inject.Singleton;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Module
public abstract class PluginFrameModule {

    @Provides
    static PluginFrame.Options providePluginFrameOptions(Config config) {
        final var pluginDir = Optional.ofNullable(config.pluginDirectory).orElse(Path.of("plugins"));

        return new PluginFrame.Options(pluginDir, config.loadPluginsOnStartup);
    }

    @Singleton
    @Provides
    static DefaultClassLoaderContainer provideClassLoaderContainer() {
        return new DefaultClassLoaderContainer();
    }

    @Singleton
    @Binds
    abstract PluginContainer bindPluginContainer(FabricPluginContainer logger);

    @Singleton
    @Provides
    static PluginBootstrap providePluginBootstrap(PluginDiscoveryService discoveryService, PluginContainer container) {
        return new OrderedPluginBootstrap(discoveryService, container);
    }

    @Singleton
    @Provides
    static PluginManager providePluginManager(PluginDiscoveryService discoveryService, PluginContainer container) {
        return new FabricPluginManager(discoveryService, container);
    }

    @Singleton
    @Provides
    static PluginDiscoveryService provideDiscoveryService(PluginFrame.Options options, Logger logger, DefaultClassLoaderContainer clContainer) {
        final var manifestLoader = new FabricJsonManifestLoader();
        final var directoryLoader = new DirectoryPluginDiscoveryService(options.pluginDirectory(), manifestLoader, clContainer, logger);

        if (!FabricLoader.getInstance().isDevelopmentEnvironment()) return directoryLoader;

        KibuDevConfig kibuDevConfig = new KibuDevConfig(logger);
        kibuDevConfig.load();

        List<URL[]> classpath = kibuDevConfig.getPluginPaths();
        if (classpath == null) return directoryLoader;

        classpath = classpath.stream()
                .map(urls -> Arrays.stream(urls)
                        .filter(url -> urlExists(url, logger))
                        .toArray(URL[]::new))
                .filter(existingOnly -> existingOnly.length > 0)
                .collect(Collectors.toList());

        var devLoader = new ClasspathPluginDiscoveryService(classpath, manifestLoader, clContainer, logger);

        return new MultiPluginDiscoveryService(directoryLoader, devLoader);
    }

    private static boolean urlExists(URL url, Logger logger) {
        if (!"file".equals(url.getProtocol())) return true;

        final Path path;

        try {
            path = Paths.get(url.toURI());
        } catch (URISyntaxException e) {
            logger.error("Failed to parse local url {}", url, e);
            return true;
        }

        return Files.exists(path);
    }
}
