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
import work.lclpnet.mplugins.cmd.MPluginsCommand;
import work.lclpnet.mplugins.cmd.PluginSuggestions;
import work.lclpnet.mplugins.config.Config;
import work.lclpnet.mplugins.config.ConfigManager;
import work.lclpnet.mplugins.di.DaggerMPluginsComponent;
import work.lclpnet.mplugins.di.MPluginsComponent;
import work.lclpnet.mplugins.di.MPluginsModule;
import work.lclpnet.mplugins.event.PluginLifecycleEvents;
import work.lclpnet.mplugins.ext.MPluginLib;

public class MPlugins implements ModInitializer, MPluginsAPI {

	public static final String MOD_ID = "mplugins";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static final Object mutex = new Object();
	private static MPlugins instance = null;

	private MPluginsComponent component = null;
	private PluginFrame pluginFrame = null;
	private ConfigManager configManager = null;
	private volatile boolean worldReady = false;
	private MinecraftServer server = null;

	@Override
	public void onInitialize() {
		synchronized (mutex) {
			if (instance != null) return;
			instance = this;
		}

		component = DaggerMPluginsComponent.builder()
				.mPluginsModule(new MPluginsModule(LOGGER))
				.build();

		configManager = component.configManager();

		configManager.loadConfig();
		configManager.setPluginFrame(pluginFrame = component.pluginFrame());

		registerEvents();

		pluginFrame.init();
	}

	private void registerEvents() {
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

		CommandRegistrationCallback.EVENT.register(this::registerCommands);
	}

	private void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher,
								  CommandRegistryAccess registryAccess,
								  CommandManager.RegistrationEnvironment environment) {

		PluginSuggestions.init(pluginFrame);

		for (MPluginsCommand cmd : component.commands()) {
			cmd.register(dispatcher);
		}
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