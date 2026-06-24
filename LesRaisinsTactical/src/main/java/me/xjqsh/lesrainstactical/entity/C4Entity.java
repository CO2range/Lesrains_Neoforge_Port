package me.xjqsh.lesrainstactical.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class C4Entity extends ThrowableItemEntity {
    public static EntityType<C4Entity> TYPE = EntityType.Builder.<C4Entity>of(C4Entity::new, MobCategory.MISC)
            .setShouldReceiveVelocityUpdates(true)
            .setTrackingRange(64)
            .setUpdateInterval(1)
            .setCustomClientFactory(C4Entity::new)
            .sized(0.4f, 0.3f)
            .noSave()
            .noSummon()
            .fireImmune()
            .build("c4_entity");

    private static final EntityDataAccessor<Boolean> ARMED = SynchedEntityData.defineId(C4Entity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DETONATION_TIME = SynchedEntityData.defineId(C4Entity.class, EntityDataSerializers.INT);

    private double damage = 30.0;
    private float radius = 6.0f;
    private boolean destroyBlocks = true;
    private boolean isArmed = false;
    private int detonationTime = -1;

    public C4Entity(LivingEntity entity, Level level, int lifeTime) {
        super(TYPE, entity, level, lifeTime);
    }

    public C4Entity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        super(TYPE, level);
    }

    public C4Entity(EntityType<C4Entity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ARMED, false);
        this.entityData.define(DETONATION_TIME, -1);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("Armed", this.isArmed);
        pCompound.putInt("DetonationTime", this.detonationTime);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.isArmed = pCompound.getBoolean("Armed");
        this.detonationTime = pCompound.getInt("DetonationTime");
    }

    @Override
    public void tick() {
        super.tick();
        
        if (!this.level().isClientSide() && this.isArmed && this.detonationTime > 0) {
            this.detonationTime--;
            if (this.detonationTime <= 0) {
                this.explode();
            }
        }
        
        if (!this.level().isClientSide() && this.tickCount % 20 == 0 && this.isArmed) {
            double x = this.getX();
            double y = this.getY();
            double z = this.getZ();
            this.level().playSound(null, x, y, z, 
                    net.minecraft.sounds.SoundEvents.TNT_PRIMED, 
                    net.minecraft.sounds.SoundSource.BLOCKS, 0.5f, 1.0f);
        }
    }

    @Override
    public void onDeath(@Nullable HitResult hitResult) {
        if (!this.isArmed) {
            this.setArmed(true);
            this.setDetonationTime(40);
            this.setShouldBounce(false);
            this.setBrokeOnGround(true);
        }
    }

    public void explode() {
        if (!this.level().isClientSide()) {
            Vec3 pos = this.position();
            Explosion.BlockInteraction type = this.isDestroyBlocks() ?
                    Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP;

            Explosion explosion = new Explosion(this.level(), this, null, null, this.getX(), this.getY(), this.getZ(), this.getRadius(), false, type);
            explosion.finalizeExplosion(true);

            if (this.level() instanceof ServerLevel level) {
                level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, pos.x, pos.y, pos.z, 1, 0, 0, 0, 0);
                level.sendParticles(ParticleTypes.LARGE_SMOKE, pos.x, pos.y + 0.5, pos.z, 20, 0.5, 0.5, 0.5, 0.1);
            }
        }
        this.discard();
    }

    public boolean detonate() {
        if (!this.isArmed) {
            this.setArmed(true);
            this.setDetonationTime(5);
            return true;
        }
        return false;
    }

    public boolean isArmed() {
        return isArmed;
    }

    public void setArmed(boolean armed) {
        isArmed = armed;
        this.entityData.set(ARMED, armed);
    }

    public int getDetonationTime() {
        return detonationTime;
    }

    public void setDetonationTime(int detonationTime) {
        this.detonationTime = detonationTime;
        this.entityData.set(DETONATION_TIME, detonationTime);
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

    @Override
    public boolean shouldBounce() {
        return !this.isArmed && super.shouldBounce();
    }
}
