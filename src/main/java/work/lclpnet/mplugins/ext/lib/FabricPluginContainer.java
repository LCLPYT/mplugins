package work.lclpnet.mplugins.ext.lib;

import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import work.lclpnet.mplugins.event.PluginLifecycleEvents;
import work.lclpnet.plugin.DistinctPluginContainer;
import work.lclpnet.plugin.load.LoadablePlugin;
import work.lclpnet.plugin.load.LoadedPlugin;
import work.lclpnet.plugin.load.PluginLoadException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashSet;

@Singleton
public class FabricPluginContainer extends DistinctPluginContainer {

    @Inject
    public FabricPluginContainer(Logger logger) {
        super(logger);
    }

    @Override
    public void ensurePluginCanBeLoaded(LoadablePlugin loadable) throws PluginLoadException {
        super.ensurePluginCanBeLoaded(loadable);

        // check fabric manifest requirements
        if (!(loadable.getManifest() instanceof FabricPluginManifest manifest)) return;

        // check if the plugin can be loaded in the current environment
        if (manifest.env() != null && !manifest.env().isEmpty()) {  // empty means everywhere
            var env = FabricLoader.getInstance().getEnvironmentType().name().toLowerCase();

            if (!manifest.env().contains(env)) {
                throw new InvalidPluginEnvException("Tried to load plugin '%s' on %s, but it is only loadable on %s"
                        .formatted(manifest.id(), env, String.join(",", manifest.env())));
            }
        }

        // check if mod requirements are met
        if (manifest.requires() != null) {
            var missingMods = new HashSet<String>();

            for (var modId : manifest.requires()) {
                if (!FabricLoader.getInstance().isModLoaded(modId)) {
                    missingMods.add(modId);
                }
            }

            if (!missingMods.isEmpty()) {
                throw new PluginLoadException("Plugin %s needs the following mod(s) to be installed: %s"
                        .formatted(manifest.id(), String.join(",", missingMods)));
            }
        }
    }

    @Override
    protected void onPluginLoading(LoadedPlugin plugin) {
        super.onPluginLoading(plugin);

        PluginLifecycleEvents.LOADING.invoker().loading(plugin);
    }

    @Override
    protected void onPluginLoaded(LoadedPlugin plugin) {
        PluginLifecycleEvents.LOADED.invoker().loaded(plugin);
    }

    @Override
    protected void onPluginUnloading(LoadedPlugin plugin) {
        super.onPluginUnloading(plugin);

        PluginLifecycleEvents.UNLOADING.invoker().unloading(plugin);
    }

    @Override
    protected void onPluginUnloaded(LoadedPlugin plugin) {
        PluginLifecycleEvents.UNLOADED.invoker().unloaded(plugin);
    }
}
