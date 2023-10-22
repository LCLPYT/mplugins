package work.lclpnet.mplugins.ext.lib;

import work.lclpnet.mplugins.event.PluginLifecycleEvents;
import work.lclpnet.mplugins.event.PluginShutdownEvents;
import work.lclpnet.plugin.PluginContainer;
import work.lclpnet.plugin.SimplePluginManager;
import work.lclpnet.plugin.discover.PluginDiscoveryService;
import work.lclpnet.plugin.load.LoadedPlugin;

import java.util.Set;

public class FabricPluginManager extends SimplePluginManager {

    public FabricPluginManager(PluginDiscoveryService discoveryService, PluginContainer pluginContainer) {
        super(discoveryService, pluginContainer);
    }

    @Override
    public void reloadPlugins(Set<LoadedPlugin> loaded) {
        PluginLifecycleEvents.RELOADING.invoker().reloading(loaded);

        super.reloadPlugins(loaded);

        PluginLifecycleEvents.RELOADED.invoker().reloaded(loaded);
    }

    @Override
    public void shutdown() {
        PluginShutdownEvents.BEGIN.invoker().onShuttingDown(this);

        super.shutdown();

        PluginShutdownEvents.COMPLETE.invoker().onShutdown(this);
    }
}
