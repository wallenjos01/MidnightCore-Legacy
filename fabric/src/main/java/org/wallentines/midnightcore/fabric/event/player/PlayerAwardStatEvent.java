package org.wallentines.midnightcore.fabric.event.player;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;

public class PlayerAwardStatEvent extends PlayerEvent {

    private final Stat<?> stat;
    private final int amount;

    public PlayerAwardStatEvent(ServerPlayer player, Stat<?> stat, int amount) {
        super(player);
        this.stat = stat;
        this.amount = amount;
    }

    public Stat<?> getStat() {
        return stat;
    }

    public int getAmount() {
        return amount;
    }
}
