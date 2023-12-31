package org.wallentines.midnightcore.fabric.event.player;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import org.wallentines.midnightlib.event.Event;

public class PlayerInteractEvent extends Event {

    private final ServerPlayer player;
    private final ItemStack item;
    private final InteractionHand hand;
    private final InteractionType action;
    private final BlockHitResult hitResult;

    private boolean cancelled = false;
    private boolean shouldSwingArm = false;

    public PlayerInteractEvent(ServerPlayer player, ItemStack item, InteractionHand hand, InteractionType action, BlockHitResult result) {
        this.player = player;
        this.item = item;
        this.hand = hand;
        this.action = action;
        this.hitResult = result;
    }

    public ServerPlayer getPlayer() {
        return player;
    }

    public ItemStack getItem() {
        return item;
    }

    public InteractionHand getHand() {
        return hand;
    }

    public BlockHitResult getBlockHit() {
        return hitResult;
    }

    public boolean isLeftClick() {
        return action == InteractionType.ATTACK;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean shouldSwingArm() {
        return shouldSwingArm;
    }

    public void setShouldSwingArm(boolean shouldSwingArm) {
        this.shouldSwingArm = shouldSwingArm;
    }

    public enum InteractionType {

        INTERACT,
        INTERACT_BLOCK,
        ATTACK

    }
}
