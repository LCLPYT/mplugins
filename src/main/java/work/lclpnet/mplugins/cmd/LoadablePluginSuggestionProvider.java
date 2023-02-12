package work.lclpnet.mplugins.cmd;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;
import work.lclpnet.mplugins.PluginFrame;
import work.lclpnet.plugin.load.LoadablePlugin;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class LoadablePluginSuggestionProvider implements SuggestionProvider<ServerCommandSource> {

    private final PluginFrame frame;

    public LoadablePluginSuggestionProvider(PluginFrame frame) {
        this.frame = frame;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                var pluginManager = frame.getPluginManager();

                frame.getDiscoveryService().discover()
                        .filter(p -> !pluginManager.isPluginLoaded(p.getManifest().id()))
                        .map(LoadablePlugin::getSource)
                        .map(Object::toString)
                        .forEach(builder::suggest);
            } catch (IOException e) {
                frame.getLogger().error("Failed to discover plugins", e);
            }

            return builder.build();
        });
    }
}
