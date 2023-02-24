package work.lclpnet.test.hook;

import org.slf4j.Logger;
import work.lclpnet.mplugins.hook.HookListenerModule;
import work.lclpnet.mplugins.hook.HookRegistrar;
import work.lclpnet.mplugins.hook.builtin.PluginLifecycleHooks;
import work.lclpnet.plugin.load.LoadedPlugin;

public class TestHookListener implements HookListenerModule {

    private final Logger logger;

    public TestHookListener(Logger logger) {
        this.logger = logger;
    }

    private void loaded(LoadedPlugin plugin) {
        logger.info("HOOK: Plugin {} was loaded.", plugin.getId());
    }

    private void unloaded(LoadedPlugin plugin) {
        logger.info("HOOK: Plugin {} was unloaded.", plugin.getId());
    }

    private void worldStateChanged(boolean ready) {
        logger.info("HOOK: World is now {}.", ready ? "ready" : "unready");
    }

    @Override
    public void registerListeners(HookRegistrar registrar) {
        registrar.registerHook(PluginLifecycleHooks.LOADED, this::loaded);
        registrar.registerHook(PluginLifecycleHooks.UNLOADING, this::unloaded);
        registrar.registerHook(PluginLifecycleHooks.WORLD_STATE_CHANGED, this::worldStateChanged);
    }
}
