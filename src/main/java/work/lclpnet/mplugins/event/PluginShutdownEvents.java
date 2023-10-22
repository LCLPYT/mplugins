package work.lclpnet.mplugins.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import work.lclpnet.plugin.PluginManager;

public class PluginShutdownEvents {

    public static final Event<ShuttingDown> BEGIN = EventFactory.createArrayBacked(ShuttingDown.class, callbacks -> (frame) -> {
        for (var callback : callbacks) {
            callback.onShuttingDown(frame);
        }
    });

    public static final Event<Shutdown> COMPLETE = EventFactory.createArrayBacked(Shutdown.class, callbacks -> (frame) -> {
        for (var callback : callbacks) {
            callback.onShutdown(frame);
        }
    });

    private PluginShutdownEvents() {}

    public interface ShuttingDown {
        void onShuttingDown(PluginManager manager);
    }

    public interface Shutdown {
        void onShutdown(PluginManager manager);
    }
}
