package work.lclpnet.mplugins;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.lclpnet.mplugins.config.Config;
import work.lclpnet.mplugins.config.ConfigService;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public class MPlugins implements ModInitializer, MPluginsAPI {

	public static final String MOD_ID = "mplugins";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static final Object mutex = new Object();
	private static MPlugins instance = null;

	private Config config = null;
	private PluginFrame pluginFrame = null;

	@Override
	public void onInitialize() {
		synchronized (mutex) {
			if (instance != null) return;
			instance = this;
		}

		loadConfig();
		createPluginFrame();

		LOGGER.info("Loading plugins...");
		pluginFrame.init();
		LOGGER.info("Plugins have been loaded.");
	}

	private void createPluginFrame() {
		final var pluginDir = Optional.ofNullable(config.pluginDirectory)
				.orElse(Path.of("plugins"));

		pluginFrame = new PluginFrame(pluginDir, LOGGER);
	}

	private void loadConfig() {
		try {
			config = ConfigService.loadConfig(LOGGER);
		} catch (IOException e) {
			throw new RuntimeException("Failed to load the config", e);
		}
	}

	@Override
	public PluginFrame getPluginFrame() {
		return pluginFrame;
	}

	@Override
	public Config getConfig() {
		return config;
	}

	public static MPluginsAPI getAPI() {
		if (instance == null) throw new IllegalStateException("Mod not initialized");

		return instance;
	}

	public static Identifier identifier(String path) {
		return new Identifier(MOD_ID, path);
	}

	public static Identifier identifier(String format, Object... substitutes) {
		return identifier(String.format(format, substitutes));
	}
}