package me.xjqsh.lesrainstactical.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PlayMessages;

public class SmokeGrenadeEntity extends ThrowableItemEntity {
    public static EntityType<SmokeGrenadeEntity> TYPE = EntityType.Builder.<SmokeGrenadeEntity>of(SmokeGrenadeEntity::new, MobCategory.MISC)
            .setShouldReceiveVelocityUpdates(true)
            .setTrackingRange(64)
            .setUpdateInterval(1)
            .setCustomClientFactory(SmokeGrenadeEntity::new)
            .sized(0.3f, 0.3f)
            .noSave()
            .noSummon()
            .fireImmune()
            .build("smoke_grenade_entity");

    public SmokeGrenadeEntity(LivingEntity entity, Level level, int lifeTime) {
        super(TYPE, entity, level, lifeTime);
    }

    public SmokeGrenadeEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        super(TYPE, level);
    }

    public SmokeGrenadeEntity(EntityType<SmokeGrenadeEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            if (this.tickCount >= 40) {
                double x = this.getX();
                double y = this.getY();
                double z = this.getZ();
                for (int i = 0; i < 16; i++) {
                    double offsetX = this.random.triangle(0, 5.5);
                    double offsetY = this.random.triangle(0, 4.5);
                    double offsetZ = this.random.triangle(0, 5.5);
                    this.level().addParticle(ParticleTypes.SMOKE, true, x + offsetX, y + offsetY, z + offsetZ, 0.0D, 0.0D, 0.0D);
                }
            }
        } else {
            if (tickCount == 40) {
                playThrowableSound("release", 16.0f, 1.0f);
            }
        }
    }
}
