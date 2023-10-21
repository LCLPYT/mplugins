package work.lclpnet.mplugins.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import work.lclpnet.plugin.load.LoadedPlugin;

import java.util.Set;

public class PluginLifecycleEvents {

    public static final Event<Loading> LOADING = EventFactory.createArrayBacked(Loading.class,
            listeners -> (plugin) -> {
                for (var listener : listeners) {
                    listener.loading(plugin);
                }
            });

    public static final Event<Loaded> LOADED = EventFactory.createArrayBacked(Loaded.class,
            listeners -> (plugin) -> {
                for (var listener : listeners) {
                    listener.loaded(plugin);
                }
            });

    public static final Event<Unloading> UNLOADING = EventFactory.createArrayBacked(Unloading.class,
            listeners -> (plugin) -> {
                for (var listener : listeners) {
                    listener.unloading(plugin);
                }
            });

    public static final Event<Unloaded> UNLOADED = EventFactory.createArrayBacked(Unloaded.class,
            listeners -> (plugin) -> {
                for (var listener : listeners) {
                    listener.unloaded(plugin);
                }
            });

    public static final Event<Reloading> RELOADING = EventFactory.createArrayBacked(Reloading.class,
            listeners -> (plugins) -> {
                for (var listener : listeners) {
                    listener.reloading(plugins);
                }
            });

    public static final Event<Reloaded> RELOADED = EventFactory.createArrayBacked(Reloaded.class,
            listeners -> (plugins) -> {
                for (var listener : listeners) {
                    listener.reloaded(plugins);
                }
            });

    public static final Event<WorldStateChange> WORLD_STATE_CHANGED = EventFactory.createArrayBacked(WorldStateChange.class,
            listeners -> (ready) -> {
                for (var listener : listeners) {
                    listener.onWorldStateChanged(ready);
                }
            });

    public interface Loading {
        void loading(LoadedPlugin plugin);
    }

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

    public interface Reloading {
        void reloading(Set<? extends LoadedPlugin> reloading);
    }

    public interface Reloaded {
        void reloaded(Set<? extends LoadedPlugin> reloaded);
    }
}
