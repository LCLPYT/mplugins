package work.lclpnet.mplugins.util;

import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import work.lclpnet.mplugins.MPluginsAPI;
import work.lclpnet.mplugins.PluginFrame;
import work.lclpnet.plugin.PluginManager;
import work.lclpnet.plugin.load.LoadedPlugin;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public class FileSystemWatcher {

    private static final int ACCUMULATION_TIME = 500;
    private final Path directory;
    private final Supplier<PluginFrame> pluginFrame;
    private final Logger logger;
    private Thread watcherThread = null;
    private Thread accumulationTimer = null;
    private final Set<LoadedPlugin> updatedPlugins = new HashSet<>();

    public FileSystemWatcher(Path directory, Supplier<PluginFrame> pluginFrame, Logger logger) {
        this.directory = directory;
        this.pluginFrame = pluginFrame;
        this.logger = logger;
    }

    public void enable() {
        synchronized (this) {
            if (watcherThread != null) return;

            watcherThread = new Thread(this::watch, "mplugins fs watcher");
            watcherThread.start();
        }
    }

    public void disable() {
        synchronized (this) {
            if (watcherThread == null) return;

            watcherThread.interrupt();

            try {
                watcherThread.join();
            } catch (InterruptedException e) {
                logger.error("Interrupted while waiting for watcher thread to die", e);
            } finally {
                watcherThread = null;
            }
        }
    }

    private void watch() {
        try (final WatchService watcher = FileSystems.getDefault().newWatchService()) {
            directory.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);

            // poll fs events
            while (true) {
                WatchKey next = watcher.take();

                var events = next.pollEvents();

                for (var event : events) {
                    var ctx = event.context();
                    if (!(ctx instanceof Path file)) continue;

                    Path path = directory.resolve(file);
                    var plugin = getPluginLoadedPlugin(path);
                    if (plugin.isEmpty()) continue;

                    updatedPlugins.add(plugin.get());

                    synchronized (this) {
                        accumulate();
                    }
                }

                next.reset();
            }
        } catch (IOException e) {
            logger.error("File system watch error", e);
        } catch (InterruptedException ignored) {}
    }

    private Optional<LoadedPlugin> getPluginLoadedPlugin(Path path) {
        if (path == null) return Optional.empty();

        PluginFrame frame = pluginFrame.get();
        if (frame == null) return Optional.empty();  // plugin frame is inactive

        PluginManager pluginManager = frame.getPluginManager();

        return pluginManager.getPlugins().stream()
                .filter(plugin -> path.equals(plugin.getSource()))
                .findAny();
    }

    private void accumulate() {
        synchronized (this) {
            if (accumulationTimer != null) return;

            accumulationTimer = new Thread(this::timeoutAccumulation, "mplugins fs event accumulator");
        }

        accumulationTimer.start();
    }

    private void timeoutAccumulation() {
        try {
            Thread.sleep(ACCUMULATION_TIME);
        } catch (InterruptedException e) {
            return;  // cancelled
        }

        synchronized (this) {
            accumulationTimer = null;
        }

        submitWork();
    }

    private void submitWork() {
        final Set<LoadedPlugin> updated;

        synchronized (this) {
            updated = new HashSet<>(updatedPlugins);
            updatedPlugins.clear();
        }

        if (updated.isEmpty()) return;

        MinecraftServer server = MPluginsAPI.get().getServer();
        if (server == null) return;

        // reload plugin server tick
        server.submit(() -> {
            PluginFrame frame = pluginFrame.get();
            if (frame == null) return;  // plugin frame is inactive

            PluginManager pluginManager = frame.getPluginManager();
            pluginManager.reloadPlugins(updated);
        });
    }
}
