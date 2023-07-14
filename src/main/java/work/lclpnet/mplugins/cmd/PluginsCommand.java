package work.lclpnet.mplugins.cmd;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import work.lclpnet.plugin.PluginManager;

import javax.inject.Inject;

import static net.minecraft.server.command.CommandManager.literal;

public class PluginsCommand implements MPluginsCommand {

    private final PluginManager pluginManager;

    @Inject
    public PluginsCommand(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(command("plugins"));
        dispatcher.register(command("pl"));
    }

    private LiteralArgumentBuilder<ServerCommandSource> command(String literal) {
        return literal(literal)
                .requires(s -> s.hasPermissionLevel(2))
                .executes(this::listPlugins);
    }

    private Integer listPlugins(CommandContext<ServerCommandSource> ctx) {
        var plugins = pluginManager.getPlugins();
        if (plugins.isEmpty()) {
            ctx.getSource().sendMessage(Text.literal("No plugins loaded."));
            return 0;
        }

        var msg = Text.literal("Loaded plugins (%s): ".formatted(plugins.size()));
        var first = true;

        for (var plugin : plugins) {
           if (first) first = false;
           else msg.append(Text.literal(", ").formatted(Formatting.WHITE));

           msg.append(Text.literal(plugin.getId()).formatted(Formatting.GREEN));
        }

        ctx.getSource().sendMessage(msg);

        return 0;
    }
}
