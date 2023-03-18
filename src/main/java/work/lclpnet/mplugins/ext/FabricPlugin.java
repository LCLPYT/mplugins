package work.lclpnet.mplugins.ext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.lclpnet.mplugins.MPlugins;
import work.lclpnet.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class FabricPlugin implements Plugin, PluginUnloader {

    private final List<Unloadable> unloadables = new ArrayList<>();
    private Logger logger;

    @Override
    public final void load() {
        var plugin = MPlugins.getAPI().getPluginFrame().getPluginManager().getPlugin(this);

        if (plugin.isPresent()) {
            logger = LoggerFactory.getLogger(plugin.get().getId());
        } else {
            logger = LoggerFactory.getLogger(this.getClass());
        }

        loadFabricPlugin();
    }

    /**
     * Called on plugin load.
     * Use this as an entrypoint for initialization.
     * <p>
     * If you need other entries, you can implement provided interfaces such as {@link WorldStateListener}
     * in order to receive world ready events.
     */
    public void loadFabricPlugin() {
        // no-op
    }

    @Override
    public final void unload() {
        unloadFabricPlugin();

        unregisterAll();
    }

    /**
     * Called on plugin unload.
     */
    public void unloadFabricPlugin() {
        // no-op
    }

    private void unregisterAll() {
        synchronized (unloadables) {
            for (var unloadable : unloadables) {
                try {
                    unloadable.unload();
                } catch (Throwable t) {
                    logger.error("Failed to unregister", t);
                }
            }
        }
    }

    @Override
    public void registerUnloadable(Unloadable unloadable) {
        if (unloadable == null) return;

        synchronized (unloadables) {
            unloadables.add(unloadable);
        }
    }

    public Logger getLogger() {
        return logger;
    }
}
