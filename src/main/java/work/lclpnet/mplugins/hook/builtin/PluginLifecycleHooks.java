package work.lclpnet.mplugins.hook.builtin;

import work.lclpnet.mplugins.hook.Hook;
import work.lclpnet.mplugins.hook.HookFactory;
import work.lclpnet.plugin.load.LoadedPlugin;

public class PluginLifecycleHooks {

    public static final Hook<Loaded> LOADED = HookFactory.createArrayBacked(Loaded.class,
            listeners -> (plugin) -> {
                for (var listener : listeners) {
                    listener.loaded(plugin);
                }
            });

    public static final Hook<Unloading> UNLOADING = HookFactory.createArrayBacked(Unloading.class,
            listeners -> (plugin) -> {
                for (var listener : listeners) {
                    listener.unloading(plugin);
                }
            });

    public static final Hook<Unloaded> UNLOADED = HookFactory.createArrayBacked(Unloaded.class,
            listeners -> (plugin) -> {
                for (var listener : listeners) {
                    listener.unloaded(plugin);
                }
            });

    public static final Hook<WorldStateChange> WORLD_STATE_CHANGED = HookFactory.createArrayBacked(WorldStateChange.class,
            listeners -> (ready) -> {
                for (var listener : listeners) {
                    listener.onWorldStateChanged(ready);
                }
            });

    public interface Loaded {
        void loaded(LoadedPlugin plugin);
    }

    public interface Unloading {
        void unloading(LoadedPlugin plugin);
    }

    public interface Unloaded {
        void unloaded(LoadedPlugin plugin);
    }

    public interface WorldStateChange {
        void onWorldStateChanged(boolean ready);
    }
}
