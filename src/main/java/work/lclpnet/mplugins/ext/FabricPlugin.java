package work.lclpnet.mplugins.ext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.lclpnet.mplugins.MPlugins;
import work.lclpnet.mplugins.hook.Hook;
import work.lclpnet.mplugins.hook.HookListenerContainer;
import work.lclpnet.mplugins.hook.HookListenerModule;
import work.lclpnet.mplugins.hook.HookRegistrar;
import work.lclpnet.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class FabricPlugin implements Plugin, HookRegistrar, PluginUnloader {

    protected final HookRegistrar hookRegistrar;
    private final List<Unloadable> unloadables = new ArrayList<>();
    private Logger logger;

    // plugins need to have a constructor without arguments to be loaded by plugins4j
    public FabricPlugin() {
        this(new HookListenerContainer());
    }

    public FabricPlugin(HookRegistrar hookRegistrar) {
        this.hookRegistrar = hookRegistrar;

        if (this.hookRegistrar instanceof Unloadable unloadable) {
            registerUnloadable(unloadable);
        }
    }

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
     * <p>
     * Hooks registered with {@link FabricPlugin#registerHook(Hook, Object)} are automatically unregistered,
     * implementations do not have to manually unregister them.
     */
    public void unloadFabricPlugin() {
        // no-op
    }

    @Override
    public <T> void registerHook(Hook<T> hook, T listener) {
        hookRegistrar.registerHook(hook, listener);
    }

    @Override
    public <T> void unregisterHook(Hook<T> hook, T listener) {
        hookRegistrar.unregisterHook(hook, listener);
    }

    @Override
    public void unregisterAllHooks() {
        hookRegistrar.unregisterAllHooks();
    }

    public void registerHooks(HookListenerModule listener) {
        if (listener != null) {
            listener.registerListeners(hookRegistrar);
        }
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
