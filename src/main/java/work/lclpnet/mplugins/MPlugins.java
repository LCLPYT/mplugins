package work.lclpnet.mplugins;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.lclpnet.mplugins.cmd.*;
import work.lclpnet.mplugins.config.Config;
import work.lclpnet.mplugins.config.ConfigService;
import work.lclpnet.mplugins.hook.builtin.PluginLifecycleHooks;

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
	private volatile boolean worldReady = false;

	@Override
	public void onInitialize() {
		synchronized (mutex) {
			if (instance != null) return;
			instance = this;
		}

		loadConfig();
		createPluginFrame();

		pluginFrame.init();

		CommandRegistrationCallback.EVENT.register(this::registerCommands);
	}

	private void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher,
								  CommandRegistryAccess registryAccess,
								  CommandManager.RegistrationEnvironment environment) {

		PluginSuggestions.init(pluginFrame);

		final var pluginManager = pluginFrame.getPluginManager();

		new PluginsCommand(pluginManager).register(dispatcher);
		new ReloadCommand(pluginManager).register(dispatcher);

		if (config.enableLoadCommand) new LoadCommand(pluginManager).register(dispatcher);
		if (config.enableUnloadCommand) new UnloadCommand(pluginManager).register(dispatcher);
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

	@Override
	public boolean isWorldReady() {
		synchronized (mutex) {
			return worldReady;
		}
	}

	@Override
	public void setWorldReady(boolean ready) {
		synchronized (mutex) {
			if (this.worldReady != ready) {
				this.worldReady = ready;

				PluginLifecycleHooks.WORLD_STATE_CHANGED.invoker().onWorldStateChanged(ready);
			}
		}
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