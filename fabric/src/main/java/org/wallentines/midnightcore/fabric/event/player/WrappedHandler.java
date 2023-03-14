package org.wallentines.midnightcore.fabric.event.player;

import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.wallentines.midnightlib.event.Event;

public record WrappedHandler(ServerPlayer player, Entity entity, ServerboundInteractPacket.Handler other) implements ServerboundInteractPacket.Handler {

    private void handleInteract(InteractionHand hand, Vec3 location) {

        PlayerInteractEntityEvent event = new PlayerInteractEntityEvent(player, entity, hand, location);
        Event.invoke(event);

        if (event.isCancelled()) {
            if (event.shouldSwingArm()) {
                player.swing(hand, true);
            }
        } else {
            other.onInteraction(hand);
        }
    }

    @Override
    public void onInteraction(@NotNull InteractionHand interactionHand) {
        handleInteract(interactionHand, null);
    }

    @Override
    public void onInteraction(@NotNull InteractionHand interactionHand, @NotNull Vec3 vec3) {
        handleInteract(interactionHand, vec3);
    }

    @Override
    public void onAttack() {

        PlayerAttackEntityEvent event = new PlayerAttackEntityEvent(player, entity);
        Event.invoke(event);

        if (!event.isCancelled()) {
            other.onAttack();
        }
    }

}
