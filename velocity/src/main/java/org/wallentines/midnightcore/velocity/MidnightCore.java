package org.wallentines.midnightcore.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.wallentines.mdcfg.codec.JSONCodec;
import org.wallentines.mdcfg.serializer.ConfigContext;
import org.wallentines.midnightcore.common.Constants;
import org.wallentines.midnightcore.common.MidnightCoreImpl;
import org.wallentines.midnightcore.api.Registries;
import org.wallentines.midnightcore.common.module.extension.ExtensionHelper;
import org.wallentines.midnightcore.velocity.event.MidnightCoreInitializeEvent;
import org.wallentines.midnightcore.velocity.item.DummyItem;
import org.wallentines.midnightcore.velocity.module.extension.VelocityExtensionModule;
import org.wallentines.midnightcore.velocity.module.globaljoin.GlobalJoinModule;
import org.wallentines.midnightcore.velocity.module.lastserver.LastServerModule;
import org.wallentines.midnightcore.velocity.module.messaging.VelocityMessagingModule;
import org.wallentines.midnightcore.velocity.server.VelocityServer;
import org.wallentines.midnightlib.Version;
import org.wallentines.mdcfg.ConfigSection;
import org.wallentines.midnightlib.event.Event;

import java.nio.file.Path;

@Plugin(id = "midnightcore", name = "MidnightCore", version = "1.0.0", authors = {"M1dnight_Ninja"})
public class MidnightCore {

    private static MidnightCore INSTANCE;

    private final ProxyServer server;
    private final Path dataFolder;

    @Inject
    public MidnightCore(ProxyServer server, @DataDirectory Path dataFolder) {

        INSTANCE = this;

        this.server = server;
        this.dataFolder = dataFolder;

    }

    @Subscribe(order= PostOrder.FIRST)
    public void onInitialize(ProxyInitializeEvent event) {

        Constants.registerDefaults();

        ConfigSection langDefaults = JSONCodec.minified().decode(ConfigContext.INSTANCE, getClass().getResourceAsStream("/lang/en_us.json")).asSection();

        MidnightCoreImpl api = new MidnightCoreImpl(
                dataFolder,
                Version.SERIALIZER.deserialize("1.19.3"),
                langDefaults,
                DummyItem::new,
                (title) -> null,
                (id, title) -> null
        );

        // Register Velocity Modules
        Registries.MODULE_REGISTRY.register(VelocityMessagingModule.ID, VelocityMessagingModule.MODULE_INFO);
        Registries.MODULE_REGISTRY.register(LastServerModule.ID, LastServerModule.MODULE_INFO);
        Registries.MODULE_REGISTRY.register(GlobalJoinModule.ID, GlobalJoinModule.MODULE_INFO);
        Registries.MODULE_REGISTRY.register(ExtensionHelper.ID, VelocityExtensionModule.MODULE_INFO);

        api.setActiveServer(new VelocityServer(api, server, this));

        Event.invoke(new MidnightCoreInitializeEvent(this));
    }

    public static MidnightCore getInstance() {
        return INSTANCE;
    }

    public ProxyServer getServer() {
        return server;
    }
}
