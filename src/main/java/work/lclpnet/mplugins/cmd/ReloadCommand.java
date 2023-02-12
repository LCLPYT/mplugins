package work.lclpnet.mplugins.cmd;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import work.lclpnet.plugin.PluginManager;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ReloadCommand {

    private final PluginManager pluginManager;

    public ReloadCommand(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(node("reloadPlugins"));
        dispatcher.register(node("rl"));
    }

    private LiteralArgumentBuilder<ServerCommandSource> node(String literal) {
        return literal(literal)
                .requires(s -> s.hasPermissionLevel(4))
                .executes(this::reloadPlugins)
                .then(argument("plugin", StringArgumentType.string())
                        .suggests(PluginSuggestions.getLoadedPluginProvider())
                        .executes(this::reloadPlugin)
                );
    }

    private Integer reloadPlugin(CommandContext<ServerCommandSource> ctx) {
        var pluginId = StringArgumentType.getString(ctx, "plugin");

        ServerCommandSource src = ctx.getSource();

        var plugin = pluginManager.getPlugin(pluginId);
        if (plugin.isEmpty()) {
            src.sendError(Text.literal("There is no loaded plugin with id '%s'.".formatted(pluginId)));
            return -1;
        }

        src.sendMessage(Text.literal("Reloading plugin '%s'...".formatted(pluginId)));

        pluginManager.reloadPlugin(plugin.get());

        src.sendMessage(Text.literal("Plugin '%s' reloaded.".formatted(pluginId)));

        return 1;
    }

    private Integer reloadPlugins(CommandContext<ServerCommandSource> ctx) {
        var plugins = pluginManager.getPlugins();

        ServerCommandSource src = ctx.getSource();
        src.sendMessage(Text.literal("Reloading plugins..."));

        pluginManager.reloadPlugins(plugins);

        src.sendMessage(Text.literal("Plugins reloaded."));

        return 1;
    }
}
