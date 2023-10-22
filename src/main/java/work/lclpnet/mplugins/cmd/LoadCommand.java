package work.lclpnet.mplugins.cmd;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import work.lclpnet.mplugins.config.Config;
import work.lclpnet.plugin.PluginManager;
import work.lclpnet.plugin.load.LoadedPlugin;

import javax.inject.Inject;
import javax.inject.Provider;
import java.nio.file.Path;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class LoadCommand implements MPluginsCommand {

    private final PluginManager pluginManager;
    private final Provider<Config> configProvider;
    private final Logger logger;

    @Inject
    public LoadCommand(PluginManager pluginManager, Provider<Config> configProvider, Logger logger) {
        this.pluginManager = pluginManager;
        this.configProvider = configProvider;
        this.logger = logger;
    }

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        if (!configProvider.get().enableLoadCommand) return;

        dispatcher.register(literal("loadPlugin")
                .requires(s -> s.hasPermissionLevel(4))
                .then(argument("source", StringArgumentType.greedyString())
                        .suggests(PluginSuggestions.loadablePluginProvider())
                        .executes(this::loadPlugin)
                ));
    }

    private Integer loadPlugin(CommandContext<ServerCommandSource> ctx) {
        var pathStr = StringArgumentType.getString(ctx, "source");
        var path = Path.of(pathStr);
        var src = ctx.getSource();

        src.sendMessage(Text.literal("Loading plugin from '%s'...".formatted(path)));

        LoadedPlugin loaded;
        try {
            loaded = pluginManager.loadPlugin(path).orElse(null);
        } catch (Throwable e) {
            logger.error("Failed to load plugin from '{}'", path, e);
            src.sendError(Text.literal("Could not load plugin from '%s'.".formatted(path)));
            return -1;
        }

        if (loaded == null) {
            src.sendError(Text.literal("Could not load plugin from '%s'.".formatted(path)));
            return -1;
        }

        src.sendMessage(Text.literal("Plugin '%s' loaded.".formatted(loaded.getId())));

        return 1;
    }
}
