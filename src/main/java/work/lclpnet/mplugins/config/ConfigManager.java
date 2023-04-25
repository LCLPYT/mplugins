package work.lclpnet.mplugins.config;

import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import work.lclpnet.config.json.ConfigHandler;
import work.lclpnet.config.json.FileConfigSerializer;
import work.lclpnet.mplugins.MPlugins;
import work.lclpnet.mplugins.PluginFrame;
import work.lclpnet.mplugins.util.FileSystemWatcher;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

public class ConfigManager {

    private static final Path CONFIG_FILE = FabricLoader.getInstance().getConfigDir()
            .resolve(MPlugins.MOD_ID).resolve("config.json");

    private final ConfigHandler<Config> configHandler;
    private final AtomicReference<PluginFrame> pluginFrame = new AtomicReference<>(null);
    private final Logger logger;
    private FileSystemWatcher fsWatcher = null;

    public ConfigManager(Logger logger) {
        this.logger = logger;

        var serializer = new FileConfigSerializer<>(Config.FACTORY, logger);
        this.configHandler = new ConfigHandler<>(CONFIG_FILE, serializer, logger);
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

    public Config getConfig() {
        return configHandler.getConfig();
    }

    public void setPluginFrame(PluginFrame frame) {
        pluginFrame.set(frame);
    }
}
