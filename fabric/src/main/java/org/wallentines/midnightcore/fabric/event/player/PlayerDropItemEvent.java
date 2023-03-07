package org.wallentines.midnightcore.fabric.event.player;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class PlayerDropItemEvent extends PlayerEvent {

    private final ItemStack item;

    private boolean cancelled;

    public PlayerDropItemEvent(ServerPlayer player, ItemStack item) {
        super(player);
        this.item = item;
    }

    public ItemStack getItem() {
        return item;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
