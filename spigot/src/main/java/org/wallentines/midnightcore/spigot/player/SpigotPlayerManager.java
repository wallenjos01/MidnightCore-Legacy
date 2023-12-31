package org.wallentines.midnightcore.spigot.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.wallentines.midnightcore.api.server.MServer;
import org.wallentines.midnightcore.common.player.AbstractPlayer;
import org.wallentines.midnightcore.common.player.AbstractPlayerManger;
import org.wallentines.midnightcore.spigot.MidnightCore;

import java.util.UUID;

public class SpigotPlayerManager extends AbstractPlayerManger<Player> implements Listener {

    public SpigotPlayerManager(MServer server) {

        super(server);
        Bukkit.getServer().getPluginManager().registerEvents(this, MidnightCore.getInstance());
    }

    @Override
    protected AbstractPlayer<Player> createPlayer(UUID u) {
        return new SpigotPlayer(u, server);
    }

    @Override
    protected UUID toUUID(String name) {

        Player pl = Bukkit.getPlayer(name);
        return pl == null ? null : pl.getUniqueId();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onJoin(PlayerLoginEvent event) {
        cachePlayer(event.getPlayer().getUniqueId(), event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onLeave(PlayerQuitEvent event) {
        cleanupPlayer(event.getPlayer().getUniqueId());
    }

}
