package work.lclpnet.mplugins.cmd;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import work.lclpnet.plugin.PluginManager;

import javax.inject.Inject;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ReloadCommand implements MPluginsCommand {

    private final PluginManager pluginManager;

    @Inject
    public ReloadCommand(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(node("reloadPlugins"));
        dispatcher.register(node("rl"));
    }

    private LiteralArgumentBuilder<ServerCommandSource> node(String literal) {
        return literal(literal)
                .requires(s -> s.hasPermissionLevel(4))
                .executes(this::reloadPlugins)
                .then(argument("plugin", StringArgumentType.string())
                        .suggests(PluginSuggestions.loadedPluginProvider())
                        .executes(this::reloadPlugin)
                );
    }

    private Integer reloadPlugin(CommandContext<ServerCommandSource> ctx) {
        var optPlugin = PluginCommandUtils.getLoadedPluginArgument(ctx, pluginManager);
        if (optPlugin.isEmpty()) return -1;

        var src = ctx.getSource();
        var plugin = optPlugin.get();
        var id = plugin.getId();

        src.sendMessage(Text.literal("Reloading plugin '%s'...".formatted(id)));

        pluginManager.reloadPlugin(plugin);

        src.sendMessage(Text.literal("Plugin '%s' reloaded.".formatted(id)));

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
