package work.lclpnet.mplugins.cmd;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

public interface MPluginsCommand {

    void register(CommandDispatcher<ServerCommandSource> dispatcher);
}
