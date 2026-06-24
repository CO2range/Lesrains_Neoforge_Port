package me.xjqsh.lesrainstactical.entity;

import me.xjqsh.lesrainstactical.init.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PlayMessages;
import org.jetbrains.annotations.Nullable;

public class StunGrenadeEntity extends ThrowableItemEntity {
    public static EntityType<StunGrenadeEntity> TYPE = EntityType.Builder.<StunGrenadeEntity>of(StunGrenadeEntity::new, MobCategory.MISC)
            .setShouldReceiveVelocityUpdates(true)
            .setTrackingRange(64)
            .setUpdateInterval(1)
            .setCustomClientFactory(StunGrenadeEntity::new)
            .sized(0.3f, 0.3f)
            .noSave()
            .noSummon()
            .fireImmune()
            .build("stun_grenade_entity");

    private double blindRadius = 8.0;
    private double deafenRadius = 10.0;
    private int blindDuration = 100;
    private int deafenDuration = 200;
    private double maxBlindAngle = 60.0;

    public StunGrenadeEntity(LivingEntity entity, Level level, int lifeTime) {
        super(TYPE, entity, level, lifeTime);
    }

    public StunGrenadeEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        super(TYPE, level);
    }

    public StunGrenadeEntity(EntityType<StunGrenadeEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public void onDeath(@Nullable HitResult hitResult) {
        if (!this.level().isClientSide()) {
            AABB aabb = this.getBoundingBox().inflate(deafenRadius);
            for (Entity entity : this.level().getEntities(this, aabb, EntitySelector.NO_SPECTATORS)) {
                if (entity instanceof LivingEntity living) {
                    calculateAndApplyEffect(this, living);
                }
            }
        }
        super.onDeath(hitResult);
    }

    public static void calculateAndApplyEffect(Entity starter, LivingEntity target) {
        Vec3 p = starter.position().add(0.0, 1.0, 0.0);
        Vec3 eyes = target.getEyePosition(1.0F);
        Vec3 d1 = p.subtract(eyes);

        double distanceMax = 10.0;
        double distance = d1.length();

        if (distance > distanceMax) {
            return;
        }

        double a1 = Math.toDegrees(Math.acos(target.getViewVector(1.0F).dot(d1.normalize())));
        double angleMax = 60.0;

        if (a1 > 0 && a1 < angleMax) {
            if (isLineOfSightClear(starter, target.level(), eyes, p)) {
                int durationBlinded = (int) (100 * (1.0 - distance / distanceMax));
                if (durationBlinded > 0) {
                    target.addEffect(new MobEffectInstance(ModEffects.BLIND.get(), durationBlinded, 0, false, false));
                }
            }
        }

        int durationDeafened = (int) (200 * (1.0 - distance / distanceMax));
        if (durationDeafened > 0) {
            target.addEffect(new MobEffectInstance(ModEffects.DEAFENED.get(), durationDeafened, 0, false, false));
        }
    }

    private static boolean isLineOfSightClear(Entity starter, Level level, Vec3 start, Vec3 end) {
        return true;
    }

    public double getBlindRadius() {
        return blindRadius;
    }

    public void setBlindRadius(double blindRadius) {
        this.blindRadius = blindRadius;
    }

    public double getDeafenRadius() {
        return deafenRadius;
    }

    public void setDeafenRadius(double deafenRadius) {
        this.deafenRadius = deafenRadius;
    }

    public int getBlindDuration() {
        return blindDuration;
    }

    public void setBlindDuration(int blindDuration) {
        this.blindDuration = blindDuration;
    }

    public int getDeafenDuration() {
        return deafenDuration;
    }

    public void setDeafenDuration(int deafenDuration) {
        this.deafenDuration = deafenDuration;
    }

    public double getMaxBlindAngle() {
        return maxBlindAngle;
    }

    public void setMaxBlindAngle(double maxBlindAngle) {
        this.maxBlindAngle = maxBlindAngle;
    }
}
