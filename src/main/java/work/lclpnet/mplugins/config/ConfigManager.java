package work.lclpnet.mplugins.config;

import org.slf4j.Logger;
import work.lclpnet.config.json.ConfigHandler;
import work.lclpnet.config.json.FileConfigSerializer;
import work.lclpnet.mplugins.PluginFrame;
import work.lclpnet.mplugins.util.FileSystemWatcher;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

@Singleton
public class ConfigManager implements ConfigAccess {

    private final ConfigHandler<Config> configHandler;
    private final AtomicReference<PluginFrame> pluginFrame = new AtomicReference<>(null);
    private final Logger logger;
    private FileSystemWatcher fsWatcher = null;

    @Inject
    public ConfigManager(@Named("configPath") Path configPath, Logger logger) {
        this.logger = logger;

        var serializer = new FileConfigSerializer<>(Config.FACTORY, logger);
        this.configHandler = new ConfigHandler<>(configPath, serializer, logger);
    }

    public void loadConfig() {
        configHandler.loadConfig();

        Config config = getConfig();

        if (config.autoReloadPlugins) {
            if (fsWatcher == null) {
                fsWatcher = new FileSystemWatcher(config.pluginDirectory, pluginFrame::get, logger);
                fsWatcher.enable();
            }
        } else if (fsWatcher != null) {
            fsWatcher.disable();
            fsWatcher = null;
        }
    }

    @Override
    public Config getConfig() {
        return configHandler.getConfig();
    }

    public void setPluginFrame(PluginFrame frame) {
        pluginFrame.set(frame);
    }
}
