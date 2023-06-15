package work.lclpnet.mplugins;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.lclpnet.mplugins.cmd.*;
import work.lclpnet.mplugins.config.Config;
import work.lclpnet.mplugins.config.ConfigManager;
import work.lclpnet.mplugins.event.PluginLifecycleEvents;
import work.lclpnet.mplugins.ext.MPluginLib;

import java.nio.file.Path;
import java.util.Optional;

public class MPlugins implements ModInitializer, MPluginsAPI {

	public static final String MOD_ID = "mplugins";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static final Object mutex = new Object();
	private static MPlugins instance = null;

	private PluginFrame pluginFrame = null;
	private final ConfigManager configManager = new ConfigManager(LOGGER);
	private volatile boolean worldReady = false;
	private MinecraftServer server = null;

	@Override
	public void onInitialize() {
		synchronized (mutex) {
			if (instance != null) return;
			instance = this;
		}

		configManager.loadConfig();
		configManager.setPluginFrame(pluginFrame = createPluginFrame());

		// update server references
		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			synchronized (mutex) {
				this.server = server;
			}

			// update server references for all plugins that have been loaded so far
			pluginFrame.getPluginManager().getPlugins().forEach(MPluginLib::updateServerReference);
		});

		// update the server reference on newly loading plugins
		PluginLifecycleEvents.LOADING.register(MPluginLib::updateServerReference);

		// call world ready and unready on plugin load / unload
		PluginLifecycleEvents.LOADED.register(MPluginLib::notifyWorldReady);
		PluginLifecycleEvents.UNLOADING.register(MPluginLib::notifyWorldUnready);

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

		Config config = getConfig();
		if (config.enableLoadCommand) new LoadCommand(pluginManager).register(dispatcher);
		if (config.enableUnloadCommand) new UnloadCommand(pluginManager).register(dispatcher);
	}

	private PluginFrame createPluginFrame() {
		Config config = getConfig();

		final var pluginDir = Optional.ofNullable(config.pluginDirectory)
				.orElse(Path.of("plugins"));

		final var options = new PluginFrame.Options(pluginDir, config.loadPluginsOnStartup);

		return new PluginFrame(options, LOGGER);
	}

	@Override
	public PluginFrame getPluginFrame() {
		return pluginFrame;
	}

	@Override
	public Config getConfig() {
		return configManager.getConfig();
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

				PluginLifecycleEvents.WORLD_STATE_CHANGED.invoker().onWorldStateChanged(ready);
			}
		}
	}

	@Nullable
	@Override
	public MinecraftServer getServer() {
		return server;
	}

	static MPluginsAPI getAPI() {
		if (instance == null) throw new IllegalStateException("Mod not initialized");

		return instance;
	}
}