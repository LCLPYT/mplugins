package work.lclpnet.mplugins.di;

import dagger.Module;
import dagger.Provides;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import work.lclpnet.mplugins.MPlugins;
import work.lclpnet.mplugins.PluginFrame;
import work.lclpnet.mplugins.config.Config;
import work.lclpnet.mplugins.config.ConfigManager;
import work.lclpnet.plugin.PluginManager;

import javax.inject.Named;
import java.nio.file.Path;
import java.util.Optional;

@Module
public class MPluginsModule {

    private final Logger logger;

    public MPluginsModule(Logger logger) {
        this.logger = logger;
    }

    @Provides
    Logger provideLogger() {
        return logger;
    }

    @Provides @Named("configPath")
    Path provideConfigPath() {
        return FabricLoader.getInstance().getConfigDir().resolve(MPlugins.MOD_ID).resolve("config.json");
    }

    @Provides
    Config provideConfig(ConfigManager configManager) {
        return configManager.getConfig();
    }

    @Provides
    PluginFrame.Options providePluginFrameOptions(Config config) {
        final var pluginDir = Optional.ofNullable(config.pluginDirectory)
                .orElse(Path.of("plugins"));

        return new PluginFrame.Options(pluginDir, config.loadPluginsOnStartup);
    }

    @Provides
    PluginManager providePluginManager(PluginFrame frame) {
        return frame.getPluginManager();
    }
}
