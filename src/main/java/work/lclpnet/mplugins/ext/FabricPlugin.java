package work.lclpnet.mplugins.ext;

import org.slf4j.Logger;
import work.lclpnet.mplugins.util.MPluginsLoggerSupplier;
import work.lclpnet.mplugins.util.PluginLoggerSupplier;
import work.lclpnet.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class FabricPlugin implements Plugin, PluginUnloader {

    private final List<Unloadable> unloadables = new ArrayList<>();
    private final PluginLoggerSupplier loggerSupplier;
    private Logger logger;

    public FabricPlugin() {
        this(new MPluginsLoggerSupplier());
    }

    public FabricPlugin(PluginLoggerSupplier loggerSupplier) {
        this.loggerSupplier = loggerSupplier;
    }

    @Override
    public final void load() {
        this.logger = loggerSupplier.getLogger(this);

        loadFabricPlugin();
    }

    /**
     * Called on plugin load.
     * Use this as an entrypoint for initialization.
     * <p>
     * If you need other entries, you can implement provided interfaces such as {@link WorldStateListener}
     * in order to receive world ready events.
     */
    protected void loadFabricPlugin() {
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
    protected void unloadFabricPlugin() {
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
        if (logger == null) throw new IllegalStateException("Not loaded");
        return logger;
    }
}
