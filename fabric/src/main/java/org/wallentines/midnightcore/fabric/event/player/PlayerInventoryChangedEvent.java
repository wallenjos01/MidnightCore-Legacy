package org.wallentines.midnightcore.fabric.event.player;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class PlayerInventoryChangedEvent extends PlayerEvent {

    private final ItemStack changedItem;

    public PlayerInventoryChangedEvent(ServerPlayer player, ItemStack changedItem) {
        super(player);
        this.changedItem = changedItem;
    }

    public ItemStack getChangedItem() {
        return changedItem;
    }
}
