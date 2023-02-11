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

public class MPlugins implements ModInitializer {

	public static final String MOD_ID = "mplugins";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private Config config = null;
	private PluginFrame pluginFrame = null;

	@Override
	public void onInitialize() {
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

	public static Identifier identifier(String path) {
		return new Identifier(MOD_ID, path);
	}

	public static Identifier identifier(String format, Object... substitutes) {
		return identifier(String.format(format, substitutes));
	}
}