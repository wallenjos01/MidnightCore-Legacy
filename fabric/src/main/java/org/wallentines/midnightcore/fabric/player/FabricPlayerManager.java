package org.wallentines.midnightcore.fabric.player;

import net.minecraft.server.level.ServerPlayer;
import org.wallentines.midnightcore.api.server.MServer;
import org.wallentines.midnightcore.common.player.AbstractPlayer;
import org.wallentines.midnightcore.common.player.AbstractPlayerManger;
import org.wallentines.midnightcore.fabric.event.player.PlayerChangeSettingsEvent;
import org.wallentines.midnightcore.fabric.event.player.PlayerLeaveEvent;
import org.wallentines.midnightcore.fabric.event.player.PlayerLoginEvent;
import org.wallentines.midnightcore.fabric.server.FabricServer;
import org.wallentines.midnightlib.event.Event;

import java.util.UUID;

public class FabricPlayerManager extends AbstractPlayerManger<ServerPlayer> {

    public FabricPlayerManager(MServer server) {
        super(server);

        Event.register(PlayerLoginEvent.class, this, Integer.MIN_VALUE, event -> cachePlayer(event.getPlayer().getUUID(), event.getPlayer()));
        Event.register(PlayerLeaveEvent.class, this, Integer.MAX_VALUE, event -> cleanupPlayer(event.getPlayer().getUUID()));
        Event.register(PlayerChangeSettingsEvent.class, this, event -> FabricPlayer.wrap(event.getPlayer()).setLocale(event.getLocale()));

    }

    @Override
    protected AbstractPlayer<ServerPlayer> createPlayer(UUID u) {
        return new FabricPlayer(u, server);
    }

    @Override
    protected UUID toUUID(String name) {

        ServerPlayer spl = ((FabricServer) server).getInternal().getPlayerList().getPlayerByName(name);
        if(spl != null) return spl.getUUID();

        try {
            return UUID.fromString(name);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }


}
