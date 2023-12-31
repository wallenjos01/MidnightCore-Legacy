package org.wallentines.midnightcore.spigot.module.skin;

import com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.wallentines.midnightcore.api.module.ServerModule;
import org.wallentines.midnightcore.api.module.skin.Skin;
import org.wallentines.midnightcore.api.player.MPlayer;
import org.wallentines.midnightcore.api.server.MServer;
import org.wallentines.midnightcore.common.module.skin.AbstractSkinModule;
import org.wallentines.midnightcore.common.util.MojangUtil;
import org.wallentines.midnightcore.spigot.MidnightCore;
import org.wallentines.midnightcore.spigot.adapter.AdapterManager;
import org.wallentines.midnightcore.spigot.adapter.SkinUpdater;
import org.wallentines.midnightcore.spigot.player.SpigotPlayer;
import org.wallentines.mdcfg.ConfigSection;
import org.wallentines.midnightlib.module.ModuleInfo;

public class SpigotSkinModule extends AbstractSkinModule implements Listener {

    private SkinUpdater updater;

    @Override
    public boolean initialize(ConfigSection configuration, MServer server) {
        if(!super.initialize(configuration, server)) return false;

        updater = AdapterManager.getAdapter().getSkinUpdater();
        if(updater == null) return false;

        try {
            updater.init();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

        Bukkit.getPluginManager().registerEvents(this, MidnightCore.getInstance());

        return true;
    }

    @Override
    protected void doUpdate(MPlayer mpl, Skin skin) {

        updater.updateSkin(((SpigotPlayer) mpl).getInternal(), skin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onLogin(PlayerJoinEvent event) {

        MPlayer player = SpigotPlayer.wrap(event.getPlayer());

        GameProfile prof = AdapterManager.getAdapter().getGameProfile(event.getPlayer());
        Skin s = MojangUtil.getSkinFromProfile(prof);
        setLoginSkin(player, s);
        if(getOfflineModeSkins && !Bukkit.getServer().getOnlineMode()) {
            findOfflineModeSkin(player, prof);
        } else {
            setActiveSkin(player, s);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onLeave(PlayerQuitEvent event) {
        onLeave(SpigotPlayer.wrap(event.getPlayer()));
    }

    public static final ModuleInfo<MServer, ServerModule> MODULE_INFO = new ModuleInfo<>(SpigotSkinModule::new, ID, DEFAULT_CONFIG);
}
