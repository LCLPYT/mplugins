package work.lclpnet.mplugins.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import work.lclpnet.plugin.load.LoadedPlugin;

public class PluginLifecycleEvents {

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

    public static final Event<WorldStateChange> WORLD_STATE_CHANGED = EventFactory.createArrayBacked(WorldStateChange.class,
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
