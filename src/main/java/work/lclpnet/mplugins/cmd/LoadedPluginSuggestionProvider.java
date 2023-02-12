package work.lclpnet.mplugins.cmd;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;
import work.lclpnet.plugin.PluginManager;
import work.lclpnet.plugin.load.LoadedPlugin;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.command.CommandSource.suggestMatching;

public class LoadedPluginSuggestionProvider implements SuggestionProvider<ServerCommandSource> {

    private final PluginManager pluginManager;

    public LoadedPluginSuggestionProvider(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        suggestMatching(pluginManager.getPlugins().stream().map(LoadedPlugin::getId), builder);

        return builder.buildFuture();
    }
}
