package org.wallentines.midnightcore.fabric.mixin;

import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.wallentines.midnightcore.fabric.event.player.PlayerInventoryChangedEvent;
import org.wallentines.midnightlib.event.Event;

@Mixin(InventoryChangeTrigger.class)
public class MixinInventoryChangeTrigger {

    @Inject(method="trigger(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/item/ItemStack;III)V", at=@At("HEAD"))
    private void onTrigger(ServerPlayer serverPlayer, Inventory inventory, ItemStack itemStack, int i, int j, int k, CallbackInfo ci) {

        Event.invoke(new PlayerInventoryChangedEvent(serverPlayer, itemStack));

    }

}
