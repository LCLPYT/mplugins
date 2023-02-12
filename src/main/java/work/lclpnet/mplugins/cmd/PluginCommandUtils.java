package work.lclpnet.mplugins.cmd;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import work.lclpnet.plugin.PluginManager;
import work.lclpnet.plugin.load.LoadedPlugin;

import java.util.Optional;

public class PluginCommandUtils {

    public static Optional<LoadedPlugin> getLoadedPluginArgument(CommandContext<ServerCommandSource> ctx,
                                                                 PluginManager pluginManager) {
        var pluginId = StringArgumentType.getString(ctx, "plugin");

        var plugin = pluginManager.getPlugin(pluginId);

        if (plugin.isEmpty()) {
            ctx.getSource().sendError(Text.literal("There is no loaded plugin with id '%s'.".formatted(pluginId)));
        }

        return plugin;
    }
}
