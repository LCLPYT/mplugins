package work.lclpnet.mplugins.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import work.lclpnet.mplugins.PluginFrame;

public class PluginBootstrapEvents {

    private PluginBootstrapEvents() {}

    public static final Event<Bootstrap> BEGIN = EventFactory.createArrayBacked(Bootstrap.class, callbacks -> (frame) -> {
        for (var callback : callbacks) {
            callback.onBootstrap(frame);
        }
    });

    public static final Event<Bootstrap> COMPLETE = EventFactory.createArrayBacked(Bootstrap.class, callbacks -> (frame) -> {
        for (var callback : callbacks) {
            callback.onBootstrap(frame);
        }
    });

    public interface Bootstrap {
        void onBootstrap(PluginFrame frame);
    }
}
