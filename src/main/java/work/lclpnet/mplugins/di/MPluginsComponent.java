package work.lclpnet.mplugins.di;

import dagger.Component;
import work.lclpnet.mplugins.PluginFrame;
import work.lclpnet.mplugins.cmd.MPluginsCommand;
import work.lclpnet.mplugins.config.ConfigManager;

import javax.inject.Singleton;
import java.util.Set;

@Singleton
@Component(modules = {
        MPluginsModule.class,
        CommandsModule.class
})
public interface MPluginsComponent {

    ConfigManager configManager();

    PluginFrame pluginFrame();

    Set<MPluginsCommand> commands();
}
