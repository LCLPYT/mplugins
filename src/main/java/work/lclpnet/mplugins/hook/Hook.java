package work.lclpnet.mplugins.hook;

/**
 * Hooks are very similar to the fabric api {@link net.fabricmc.fabric.api.event.Event}s.
 * Sadly, Fabric API events cannot simply be unregistered.
 * <p>
 * However, event listeners registered by plugins need to be unregistered when the plugin unloads.
 * This is where hooks can be used instead.
 *
 * @param <T> The listener type.
 */
public interface Hook<T> {

    T invoker();

    void register(T listener);

    void unregister(T listener);
}
