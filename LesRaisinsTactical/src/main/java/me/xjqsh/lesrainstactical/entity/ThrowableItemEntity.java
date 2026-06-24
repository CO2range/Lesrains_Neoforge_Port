package me.xjqsh.lesrainstactical.entity;

import me.xjqsh.lesrainstactical.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.entity.IEntityWithSpawnPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Predicate;

public abstract class ThrowableItemEntity extends Projectile implements IEntityWithSpawnPacket {
    private static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK = SynchedEntityData.defineId(ThrowableItemEntity.class, EntityDataSerializers.ITEM_STACK);
    private int life = 100;
    private float gravity = 0.07f;
    private double bounceFactor = 0.75;
    private boolean shouldBounce = true;
    private boolean brokeOnGround = false;
    private float hitDamage = 1.0f;
    private ParticleOptions tailParticle = null;
    private Item defaultItem;

    public ThrowableItemEntity(EntityType<? extends Projectile> type, LivingEntity shooter, Level level, int lifeTime) {
        super(type, level);
        this.setPos(shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ());
        this.setOwner(shooter);
        this.life = lifeTime;
    }

    public ThrowableItemEntity(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
    }

    @NotNull
    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return IEntityWithSpawnPacket.create(this);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        super.onSyncedDataUpdated(pKey);
    }

    protected void defineSynchedData() {
        this.getEntityData().define(DATA_ITEM_STACK, ItemStack.EMPTY);
    }

    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        ItemStack itemstack = this.getItemRaw();
        if (!itemstack.isEmpty()) {
            pCompound.put("Item", itemstack.save(new CompoundTag()));
        }
    }

    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        ItemStack itemstack = ItemStack.of(pCompound.getCompound("Item"));
        this.setItem(itemstack);
    }

    protected ItemStack getItemRaw() {
        return this.getEntityData().get(DATA_ITEM_STACK);
    }

    public ItemStack getItem() {
        ItemStack itemstack = this.getItemRaw();
        return itemstack.isEmpty() ? new ItemStack(this.getDefaultItem()) : itemstack;
    }

    public void setItem(ItemStack pStack) {
        if (!pStack.is(this.getDefaultItem()) || pStack.hasTag()) {
            this.getEntityData().set(DATA_ITEM_STACK, pStack.copyWithCount(1));
        }
    }

    protected Item getDefaultItem() {
        if (defaultItem == null) {
            defaultItem = Item.byId(0);
        }
        return defaultItem;
    }

    public void setDefaultItem(Item item) {
        this.defaultItem = item;
    }

    @Override
    protected void onHit(HitResult result) {
        if (result.getType() != HitResult.Type.MISS) {
            boolean flag = this.brokeOnGround && result instanceof BlockHitResult blockHitResult
                    && blockHitResult.getDirection() == Direction.UP;
            if (!this.shouldBounce || flag) {
                this.onDeath(result);
                return;
            }
        }
        switch (result.getType()) {
            case BLOCK -> {
                BlockHitResult blockResult = (BlockHitResult) result;
                BlockPos resultPos = blockResult.getBlockPos();
                BlockState state = this.level().getBlockState(resultPos);
                SoundEvent event = state.getBlock().getSoundType(state, this.level(), resultPos, this).getStepSound();
                double speed = this.getDeltaMovement().length();
                if (speed > 0.1) {
                    this.level().playSound(null, result.getLocation().x, result.getLocation().y, result.getLocation().z,
                            event, SoundSource.AMBIENT, 2.0F, 1.0F);
                    this.level().playSound(null, result.getLocation().x, result.getLocation().y, result.getLocation().z,
                            ModSounds.GRENADE_BOUNCE.get(), SoundSource.AMBIENT, 2.0F, 1.0F);
                }

                state.onProjectileHit(this.level(), state, blockResult, this);
            }
            case ENTITY -> {
                EntityHitResult entityResult = (EntityHitResult) result;
                Entity entity = entityResult.getEntity();
                if (entity == this.getOwner() || entity == this.getVehicle()) return;
                double speed = this.getDeltaMovement().length();
                if (speed > 0.1) {
                    entity.hurt(entity.damageSources().thrown(this, this.getOwner()), this.getHitDamage());
                }
            }
            default -> {}
        }
    }

    public record BounceResult(Vec3 location, Vec3 deltaMovement) {
    }

    public BounceResult doMultiBounce(Vec3 deltaMovement) {
        Vec3 start = this.position();
        Vec3 end = start.add(deltaMovement);
        Vec3 endVecOffset = new Vec3(deltaMovement.x, deltaMovement.y, deltaMovement.z);
        for (int i = 0; i < 3; i++) {
            HitResult hitResult = this.getHitResult(start, end, endVecOffset, this::canHitEntity, this.level());
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockResult = (BlockHitResult) hitResult;
                Vec3 hit = blockResult.getLocation();
                if (blockResult.getDirection() == Direction.UP && start.y() - hit.y() < 0.01) {
                    hit = new Vec3(hit.x(), start.y(), hit.z());
                }
                if (i < 2) {
                    start = start.lerp(hit, 0.8);

                    Vec3 rest = end.subtract(start);
                    endVecOffset = this.bounce(blockResult.getDirection(), rest);
                    end = start.add(endVecOffset);

                    deltaMovement = this.bounce(blockResult.getDirection(), deltaMovement);
                } else {
                    end = start.lerp(hit, 0.8);
                    deltaMovement = Vec3.ZERO;
                }
            } else if (hitResult.getType() == HitResult.Type.ENTITY) {
                EntityHitResult entityResult = (EntityHitResult) hitResult;
                Entity entity = entityResult.getEntity();
                if (entity == this.getOwner() || entity == this.getVehicle()) break;

                Direction direction = Direction.getNearest(endVecOffset.x(), endVecOffset.y(), endVecOffset.z()).getOpposite();

                Vec3 hit = hitResult.getLocation();
                start = start.lerp(hit, 0.8);
                Vec3 rest = end.subtract(start);
                endVecOffset = this.bounce(direction, rest);
                end = start.add(endVecOffset);

                deltaMovement = this.bounce(direction, deltaMovement);
            } else if (hitResult.getType() == HitResult.Type.MISS) {
                break;
            }
            this.onHit(hitResult);
        }

        return new BounceResult(end, deltaMovement);
    }

    public Vec3 bounce(Direction direction, Vec3 deltaMovement) {
        double factor = this.getBounceFactor();
        return switch (direction.getAxis()) {
            case X -> deltaMovement.multiply(-factor/1.5, factor, factor);
            case Y -> {
                Vec3 newVec = deltaMovement.multiply(factor, -factor/2.5, factor);
                if (newVec.y() < this.getGravity()) {
                    newVec = newVec.multiply(1, 0, 1);
                }
                yield newVec;
            }
            case Z -> deltaMovement.multiply(factor, factor, -factor/1.5);
        };
    }

    public HitResult getHitResult(Vec3 start, Vec3 end, Vec3 endVecOffset, Predicate<Entity> pFilter, Level pLevel) {
        HitResult hitresult = pLevel.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (hitresult.getType() != HitResult.Type.MISS) {
            end = hitresult.getLocation();
        }

        HitResult hitresult1 = getEntityHitResult(pLevel, this, start, end, this.getBoundingBox().expandTowards(endVecOffset).inflate(1.0D), pFilter);
        if (hitresult1 != null) {
            hitresult = hitresult1;
        }

        return hitresult;
    }

    @Nullable
    public static EntityHitResult getEntityHitResult(Level pLevel, Entity pProjectile, Vec3 pStartVec, Vec3 pEndVec, AABB pBoundingBox, Predicate<Entity> pFilter) {
        double d0 = Double.MAX_VALUE;
        Entity entity = null;
        Vec3 hitPos = null;

        for(Entity entity1 : pLevel.getEntities(pProjectile, pBoundingBox, pFilter)) {
            AABB aabb = entity1.getBoundingBox().inflate(0.3);
            Optional<Vec3> optional = aabb.clip(pStartVec, pEndVec);
            if (optional.isPresent()) {
                double d1 = pStartVec.distanceToSqr(optional.get());
                if (d1 < d0) {
                    entity = entity1;
                    d0 = d1;
                    hitPos = optional.get();
                }
            }
        }

        return entity == null ? null : new EntityHitResult(entity, hitPos);
    }

    public void playBounceSound() {
    }

    @Override
    public void tick() {
        super.tick();
        var result = this.doMultiBounce(this.getDeltaMovement());

        this.checkInsideBlocks();

        Vec3 vec3 = result.deltaMovement();
        double x = result.location().x();
        double y = result.location().y();
        double z = result.location().z();

        this.setDeltaMovement(vec3);
        this.updateRotation();

        float f;
        if (this.isInWater()) {
            for(int i = 0; i < 4; ++i) {
                this.level().addParticle(ParticleTypes.BUBBLE, x - vec3.x * 0.25D, y - vec3.y * 0.25D, z - vec3.z * 0.25D, vec3.x, vec3.y, vec3.z);
            }

            f = 0.8F;
        } else {
            f = 0.99F;
        }

        this.setDeltaMovement(vec3.scale(f));
        if (!this.isNoGravity()) {
            Vec3 vec31 = this.getDeltaMovement();
            this.setDeltaMovement(vec31.x, vec31.y - (double)this.getGravity(), vec31.z);
        }

        this.setPos(x, y, z);

        if (this.tickCount >= life && life > 0) {
            if (!this.level().isClientSide()) {
                this.onDeath(null);
            }
        }

        if (this.level().isClientSide()) {
            this.renderTailParticle();
        }
    }

    public void renderTailParticle() {
        if (this.getTailParticle() != null) {
            this.level().addParticle(this.getTailParticle(), true, this.getX(), this.getY() + 0.35, this.getZ(), 0.0D, 0.01D, 0.0D);
        }
    }

    public void onDeath(@Nullable HitResult hitResult) {
        this.discard();
        if (!this.level().isClientSide()) {
            playThrowableSound("death", 16.0F, 1.0F);
        }
    }

    @Deprecated
    public void onDeath() {
        this.onDeath(null);
    }

    public void playThrowableSound(String key, float volume, float pitch) {
        // Sound playing logic - to be implemented with proper network message
    }

    public boolean shouldRenderAtSqrDistance(double pDistance) {
        double d0 = this.getBoundingBox().getSize() * 4.0D;
        if (Double.isNaN(d0)) {
            d0 = 4.0D;
        }

        d0 *= 64.0D;
        return pDistance < d0 * d0;
    }

    public void setBaseData(float gravity, double bounceFactor, boolean shouldBounce, float hitDamage, boolean brokeOnGround, ParticleOptions tailParticles) {
        this.setGravity(gravity);
        this.setBounceFactor(bounceFactor);
        this.setShouldBounce(shouldBounce);
        this.setHitDamage(hitDamage);
        this.setBrokeOnGround(brokeOnGround);
        this.setTailParticle(tailParticles);
    }

    public float getGravity() {
        return gravity;
    }

    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    public double getBounceFactor() {
        return bounceFactor;
    }

    public void setBounceFactor(double bounceFactor) {
        this.bounceFactor = bounceFactor;
    }

    public boolean shouldBounce() {
        return shouldBounce;
    }

    public void setShouldBounce(boolean shouldBounce) {
        this.shouldBounce = shouldBounce;
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public float getHitDamage() {
        return hitDamage;
    }

    public void setHitDamage(float hitDamage) {
        this.hitDamage = hitDamage;
    }

    public boolean isBrokeOnGround() {
        return brokeOnGround;
    }

    public void setBrokeOnGround(boolean brokeOnGround) {
        this.brokeOnGround = brokeOnGround;
    }

    public ParticleOptions getTailParticle() {
        return tailParticle;
    }

    public void setTailParticle(ParticleOptions tailParticle) {
        this.tailParticle = tailParticle;
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeInt(life);
        buffer.writeFloat(gravity);
        buffer.writeDouble(bounceFactor);
        buffer.writeBoolean(shouldBounce);
        buffer.writeBoolean(brokeOnGround);
        if (tailParticle != null) {
            buffer.writeBoolean(true);
            EntityDataSerializers.PARTICLE.write(buffer, tailParticle);
        } else {
            buffer.writeBoolean(false);
        }
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        life = additionalData.readInt();
        gravity = additionalData.readFloat();
        bounceFactor = additionalData.readDouble();
        shouldBounce = additionalData.readBoolean();
        brokeOnGround = additionalData.readBoolean();
        if (additionalData.readBoolean()) {
            tailParticle = EntityDataSerializers.PARTICLE.read(additionalData);
        } else {
            tailParticle = null;
        }
    }
}
