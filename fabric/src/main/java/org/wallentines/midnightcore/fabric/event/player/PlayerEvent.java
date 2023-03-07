package org.wallentines.midnightcore.fabric.event.player;

import net.minecraft.server.level.ServerPlayer;

public class PlayerEvent {

    private final ServerPlayer player;

    protected PlayerEvent(ServerPlayer player) {
        this.player = player;
    }

    public ServerPlayer getPlayer() {
        return player;
    }
}
