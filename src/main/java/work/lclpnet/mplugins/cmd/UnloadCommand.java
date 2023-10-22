package work.lclpnet.mplugins.cmd;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import work.lclpnet.mplugins.config.Config;
import work.lclpnet.plugin.PluginManager;

import javax.inject.Inject;
import javax.inject.Provider;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class UnloadCommand implements MPluginsCommand {

    private final PluginManager pluginManager;
    private final Provider<Config> configProvider;
    private final Logger logger;

    @Inject
    public UnloadCommand(PluginManager pluginManager, Provider<Config> configProvider, Logger logger) {
        this.pluginManager = pluginManager;
        this.configProvider = configProvider;
        this.logger = logger;
    }

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        if (!configProvider.get().enableUnloadCommand) return;

        dispatcher.register(literal("unloadPlugin")
                .requires(s -> s.hasPermissionLevel(4))
                .then(argument("plugin", StringArgumentType.string())
                        .suggests(PluginSuggestions.loadedPluginProvider())
                        .executes(this::unloadPlugin)
                ));
    }

    private Integer unloadPlugin(CommandContext<ServerCommandSource> ctx) {
        var optPlugin = PluginCommandUtils.getLoadedPluginArgument(ctx, pluginManager);
        if (optPlugin.isEmpty()) return -1;

        var src = ctx.getSource();
        var plugin = optPlugin.get();
        var id = plugin.getId();

        src.sendMessage(Text.literal("Unloading plugin '%s'...".formatted(id)));

        try {
            pluginManager.unloadPlugin(plugin);
        } catch (Throwable t) {
            logger.error("Failed to unload plugin '{}'", id, t);
            throw new CommandException(Text.literal("Failed to unload plugin '%s'".formatted(id)));
        }

        src.sendMessage(Text.literal("Plugin '%s' unloaded.".formatted(id)));

        return 1;
    }
}
