package work.lclpnet.mplugins.di;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;
import work.lclpnet.mplugins.cmd.*;

@Module
interface MPluginsCommandModule {

    @Binds @IntoSet
    MPluginsCommand bindLoadCommand(LoadCommand impl);

    @Binds @IntoSet
    MPluginsCommand bindUnloadCommand(UnloadCommand impl);

    @Binds @IntoSet
    MPluginsCommand bindPluginsCommand(PluginsCommand impl);

    @Binds @IntoSet
    MPluginsCommand bindReloadCommand(ReloadCommand impl);
}
