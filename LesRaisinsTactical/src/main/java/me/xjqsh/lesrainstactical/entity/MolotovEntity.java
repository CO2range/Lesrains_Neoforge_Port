package me.xjqsh.lesrainstactical.entity;

import me.xjqsh.lesrainstactical.init.ModEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PlayMessages;
import org.jetbrains.annotations.Nullable;

public class MolotovEntity extends ThrowableItemEntity {
    public static EntityType<MolotovEntity> TYPE = EntityType.Builder.<MolotovEntity>of(MolotovEntity::new, MobCategory.MISC)
            .setShouldReceiveVelocityUpdates(true)
            .setTrackingRange(64)
            .setUpdateInterval(1)
            .setCustomClientFactory(MolotovEntity::new)
            .sized(0.3f, 0.4f)
            .noSave()
            .noSummon()
            .fireImmune()
            .build("molotov_entity");

    private double damageRadius = 3.0;
    private int fireDuration = 100;
    private int flammableDuration = 300;
    private boolean destroyed = false;

    public MolotovEntity(LivingEntity entity, Level level, int lifeTime) {
        super(TYPE, entity, level, lifeTime);
    }

    public MolotovEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        super(TYPE, level);
    }

    public MolotovEntity(EntityType<MolotovEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
        
        if (this.level().isClientSide()) {
            if (!this.destroyed && this.tickCount >= 20) {
                double x = this.getX();
                double y = this.getY();
                double z = this.getZ();
                for (int i = 0; i < 3; i++) {
                    this.level().addParticle(ParticleTypes.FLAME, true, 
                            x + (this.random.nextDouble() - 0.5) * 0.5, 
                            y + 0.1, 
                            z + (this.random.nextDouble() - 0.5) * 0.5, 
                            0.0D, 0.05D, 0.0D);
                }
                this.level().addParticle(ParticleTypes.SMOKE, true, 
                        x + (this.random.nextDouble() - 0.5) * 0.3, 
                        y + 0.2, 
                        z + (this.random.nextDouble() - 0.5) * 0.3, 
                        0.0D, 0.02D, 0.0D);
            }
        } else {
            if (!this.destroyed && this.tickCount == 20) {
                playThrowableSound("break", 16.0f, 1.0f);
            }
        }
    }

    @Override
    public void onDeath(@Nullable HitResult hitResult) {
        if (!this.destroyed) {
            this.destroyed = true;
            Vec3 pos = hitResult == null ? this.position() : hitResult.getLocation();
            
            if (!this.level().isClientSide()) {
                this.createFireArea(pos);
                
                if (this.level() instanceof ServerLevel level) {
                    level.sendParticles(ParticleTypes.FLAME, pos.x, pos.y + 0.2, pos.z, 30, 0.5, 0.3, 0.5, 0.1);
                    level.sendParticles(ParticleTypes.SMOKE, pos.x, pos.y + 0.5, pos.z, 20, 0.7, 0.5, 0.7, 0.05);
                }
            }
        }
        super.onDeath(hitResult);
    }

    private void createFireArea(Vec3 pos) {
        AABB aabb = new AABB(pos.x - damageRadius, pos.y - 0.5, pos.z - damageRadius,
                            pos.x + damageRadius, pos.y + 1.0, pos.z + damageRadius);
        
        for (Entity entity : this.level().getEntities(this, aabb)) {
            if (entity instanceof LivingEntity living && !living.fireImmune()) {
                living.addEffect(new MobEffectInstance(ModEffects.FLAMMABLE.get(), flammableDuration, 0, false, false));
                living.setSecondsOnFire(fireDuration / 20);
            }
        }
    }

    public double getDamageRadius() {
        return damageRadius;
    }

    public void setDamageRadius(double damageRadius) {
        this.damageRadius = damageRadius;
    }

    public int getFireDuration() {
        return fireDuration;
    }

    public void setFireDuration(int fireDuration) {
        this.fireDuration = fireDuration;
    }

    public int getFlammableDuration() {
        return flammableDuration;
    }

    public void setFlammableDuration(int flammableDuration) {
        this.flammableDuration = flammableDuration;
    }
}
