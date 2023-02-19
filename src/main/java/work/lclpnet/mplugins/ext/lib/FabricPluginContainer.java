package work.lclpnet.mplugins.ext.lib;

import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import work.lclpnet.mplugins.hook.builtin.PluginLifecycleHooks;
import work.lclpnet.plugin.DistinctPluginContainer;
import work.lclpnet.plugin.load.LoadablePlugin;
import work.lclpnet.plugin.load.LoadedPlugin;
import work.lclpnet.plugin.load.PluginLoadException;

import java.util.HashSet;

public class FabricPluginContainer extends DistinctPluginContainer {

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
            var missingMods = new HashSet<>();

            for (var modId : manifest.requires()) {
                if (!FabricLoader.getInstance().isModLoaded(modId)) {
                    missingMods.add(modId);
                }
            }

            if (!missingMods.isEmpty()) {
                throw new PluginLoadException("Plugin %s needs the following mod(s) to be installed: %s"
                        .formatted(manifest.id(), String.join(",", manifest.requires())));
            }
        }
    }

    @Override
    protected void onPluginLoaded(LoadedPlugin plugin) {
        super.onPluginLoaded(plugin);

        PluginLifecycleHooks.LOADED.invoker().loaded(plugin);
    }

    @Override
    protected void onPluginUnloaded(LoadedPlugin plugin) {
        super.onPluginUnloaded(plugin);

        PluginLifecycleHooks.UNLOADED.invoker().unloaded(plugin);
    }
}
