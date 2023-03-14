package org.wallentines.midnightcore.fabric.event.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.wallentines.midnightlib.event.Event;

public class EntityDeathEvent extends Event {

    private final LivingEntity dead;
    private final EntityDamageEvent lastDamageSource;
    private DamageSource source;

    public EntityDeathEvent(LivingEntity dead, EntityDamageEvent lastDamageSource, DamageSource source) {
        this.dead = dead;
        this.lastDamageSource = lastDamageSource;
        this.source = source;
    }

    public LivingEntity getDead() {
        return dead;
    }

    public EntityDamageEvent getLastDamageSource() {
        return lastDamageSource;
    }

    public DamageSource getSource() {
        return source;
    }

    public void setSource(DamageSource source) {
        this.source = source;
    }

}
