package org.wallentines.midnightcore.fabric.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.wallentines.midnightcore.fabric.event.entity.EntityDamageEvent;
import org.wallentines.midnightcore.fabric.event.entity.EntityDeathEvent;
import org.wallentines.midnightcore.fabric.event.entity.EntityEatEvent;
import org.wallentines.midnightlib.event.Event;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {

    private EntityDamageEvent mcore$lastEvent;

    @Inject(method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", at=@At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;die(Lnet/minecraft/world/damagesource/DamageSource;)V"))
    public void damageDeath(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {

        LivingEntity le = (LivingEntity) (Object) this;
        Event.invoke(new EntityDeathEvent(le, mcore$lastEvent, source));

        mcore$lastEvent = null;
    }

    @Inject(method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", at=@At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V"), cancellable = true)
    public void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {

        LivingEntity le = (LivingEntity) (Object) this;

        EntityDamageEvent ev = new EntityDamageEvent(le, source, amount);
        Event.invoke(ev);

        if(ev.isCancelled()) {
            info.setReturnValue(false);
            info.cancel();
            return;
        }

        mcore$lastEvent = ev;
    }

    @Inject(method = "eat", at=@At(value="INVOKE", target="Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"), cancellable = true)
    private void onEat(Level level, ItemStack itemStack, CallbackInfoReturnable<ItemStack> cir) {

        LivingEntity le = (LivingEntity) (Object) this;

        EntityEatEvent event = new EntityEatEvent(le, itemStack);
        Event.invoke(event);

        if(event.isCancelled()) {
            le.gameEvent(GameEvent.EAT);
            cir.setReturnValue(itemStack);
        }

    }

}
