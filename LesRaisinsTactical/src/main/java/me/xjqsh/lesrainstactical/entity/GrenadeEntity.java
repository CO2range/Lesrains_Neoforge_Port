package me.xjqsh.lesrainstactical.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.network.PlayMessages;

public class GrenadeEntity extends ThrowableItemEntity {
    public static EntityType<GrenadeEntity> TYPE = EntityType.Builder.<GrenadeEntity>of(GrenadeEntity::new, MobCategory.MISC)
            .setShouldReceiveVelocityUpdates(true)
            .setTrackingRange(64)
            .setUpdateInterval(1)
            .setCustomClientFactory(GrenadeEntity::new)
            .sized(0.3f, 0.3f)
            .noSave()
            .noSummon()
            .fireImmune()
            .build("grenade_entity");

    private double damage = 18.0;
    private float radius = 4.5f;
    private boolean destroyBlocks = false;
    private float destroyMultiplier = 1.0f;
    private double screenShakeTime = 20;
    private double screenShakeAmplitude = 50;
    private boolean triggerOnExplode = false;
    private boolean exploded = false;

    public GrenadeEntity(EntityType<? extends GrenadeEntity> type, LivingEntity entity, Level level, int lifeTime) {
        super(type, entity, level, lifeTime);
    }

    public GrenadeEntity(LivingEntity entity, Level level, int lifeTime) {
        super(TYPE, entity, level, lifeTime);
    }

    public GrenadeEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        super(TYPE, level);
    }

    public GrenadeEntity(EntityType<? extends GrenadeEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public void onDeath(HitResult hitResult) {
        exploded = true;
        Vec3 pos = hitResult == null ? this.position() : this.position().lerp(hitResult.getLocation(), 0.8);
        if (!this.level().isClientSide()) {
            Explosion.BlockInteraction type = this.isDestroyBlocks() ?
                    Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP;
            
            Explosion explosion = new Explosion(this.level(), this, null, null, this.getX(), this.getY(), this.getZ(), this.getRadius(), false, type);
            explosion.finalizeExplosion(true);
            
            if (EventHooks.onExplosionStart(this.level(), explosion)) {
                return;
            }
            
            if (this.level() instanceof ServerLevel level) {
                double x = pos.x();
                double y = pos.y();
                double z = pos.z();
                level.sendParticles(ParticleTypes.FLASH, x, y + 0.5, z, 50, 0.2, 0.2, 0.2, 20);
                level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, x, y + 1, z, 5, 0.7, 0.7, 0.7, 1);
            }
        }
        super.onDeath(hitResult);
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (!this.level().isClientSide() && triggerOnExplode && pSource.is(DamageTypeTags.IS_EXPLOSION) && !exploded) {
            this.setLife(this.tickCount + 3);
            return true;
        }
        return super.hurt(pSource, pAmount);
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public boolean isDestroyBlocks() {
        return destroyBlocks;
    }

    public void setDestroyBlocks(boolean destroyBlocks) {
        this.destroyBlocks = destroyBlocks;
    }

    public double getScreenShakeTime() {
        return screenShakeTime;
    }

    public void setScreenShakeTime(double screenShakeTime) {
        this.screenShakeTime = screenShakeTime;
    }

    public double getScreenShakeAmplitude() {
        return screenShakeAmplitude;
    }

    public void setScreenShakeAmplitude(double screenShakeAmplitude) {
        this.screenShakeAmplitude = screenShakeAmplitude;
    }

    public float getDestroyMultiplier() {
        return destroyMultiplier;
    }

    public void setDestroyMultiplier(float destroyMultiplier) {
        this.destroyMultiplier = destroyMultiplier;
    }

    public void setTriggerOnExplode(boolean triggerOnExplode) {
        this.triggerOnExplode = triggerOnExplode;
    }

    public boolean isTriggerOnExplode() {
        return triggerOnExplode;
    }
}
