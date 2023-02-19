package work.lclpnet.mplugins.hook;

import work.lclpnet.mplugins.ext.Unloadable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HookListenerContainer implements HookRegistrar, Unloadable {

    private final Map<Hook<?>, List<?>> eventListeners = new HashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public final <T> void registerHook(Hook<T> hook, T listener) {
        if (hook == null || listener == null) return;

        synchronized (eventListeners) {
            var listeners = (List<T>) eventListeners.computeIfAbsent(hook, h -> new ArrayList<T>());
            listeners.add(listener);
        }

        hook.register(listener);
    }

    @Override
    public final <T> void unregisterHook(Hook<T> hook, T listener) {
        if (listener == null) return;

        synchronized (eventListeners) {
            var listeners = eventListeners.get(hook);
            if (listeners == null) return;

            listeners.remove(listener);
        }
    }

    @Override
    public void unregisterAllHooks() {
        unload();
    }

    @SuppressWarnings("unchecked")
    private <T> void unregisterListenersOf(Hook<T> hook) {
        var listeners = (List<T>) eventListeners.get(hook);
        listeners.forEach(hook::unregister);
    }

    @Override
    public void unload() {
        synchronized (eventListeners) {
            for (Hook<?> hook : eventListeners.keySet()) {
                unregisterListenersOf(hook);
            }

            eventListeners.clear();
        }
    }
}
