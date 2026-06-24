package me.xjqsh.lesrainstactical.compat.sable;

import me.xjqsh.lesrainstactical.compat.aeronautics.BlockMassTable;
import me.xjqsh.lesrainstactical.compat.aeronautics.HoneyGlueHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SableExplosionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger("LesRaisinsTactical:Explosion");

    public static void handleExplosion(Level level, Vec3 center, Entity owner,
                                        float radius, float damage,
                                        boolean destroysBlocks,
                                        PhysicsProjectile.ProjectileType type) {
        if (level.isClientSide()) return;

        LOGGER.debug("Handling Sable explosion at {} with radius {}", center, radius);

        float coreRadius = radius * 0.4f;
        float midRadius = radius * 0.75f;
        float outerRadius = radius;

        applyRadialImpulseToSubLevels(level, center, radius, damage);

        if (destroysBlocks) {
            handleCoreExplosion(level, center, coreRadius, damage);
        }

        handleMidExplosion(level, center, coreRadius, midRadius, damage * 0.5f);
        handleOuterExplosion(level, center, midRadius, outerRadius, damage * 0.2f);
        handleEntityDamage(level, center, owner, radius, damage, type);
        spawnExplosionParticles(level, center, type);
        playExplosionSound(level, center, type);
    }

    private static void handleCoreExplosion(Level level, Vec3 center, float radius, float damage) {
        Set<BlockPos> blocksToBreak = new HashSet<>();
        int r = (int) Math.ceil(radius);
        BlockPos centerPos = BlockPos.containing(center);

        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    BlockPos pos = centerPos.offset(x, y, z);
                    double dist = Math.sqrt(x * x + y * y + z * z);
                    if (dist <= radius) {
                        BlockState state = level.getBlockState(pos);
                        if (state.isAir()) continue;

                        float mass = BlockMassTable.getMass(state);
                        float toughness = BlockMassTable.getToughness(state);

                        if (mass < 0) continue;

                        float breakThreshold = toughness * 10f;
                        if (damage >= breakThreshold) {
                            blocksToBreak.add(pos);
                        } else if (damage > breakThreshold * 0.3f) {
                            if (level.random.nextFloat() < (damage / breakThreshold) * 0.5f) {
                                blocksToBreak.add(pos);
                            }
                        }
                    }
                }
            }
        }

        breakSubLevelBlocks(level, blocksToBreak, center, damage);
    }

    private static void handleMidExplosion(Level level, Vec3 center, float innerR, float outerR, float damage) {
        BlockPos centerPos = BlockPos.containing(center);
        int r = (int) Math.ceil(outerR);

        int glueRemoved = 0;
        int blocksPushed = 0;

        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    BlockPos pos = centerPos.offset(x, y, z);
                    double dist = Math.sqrt(x * x + y * y + z * z);

                    if (dist > innerR && dist <= outerR) {
                        if (level.random.nextFloat() < 0.6f) {
                            if (HoneyGlueHandler.removeHoneyGlueAt(level, pos)) {
                                glueRemoved++;
                            }
                        }

                        if (level.random.nextFloat() < 0.4f) {
                            Vec3 pushDir = Vec3.atCenterOf(pos).subtract(center).normalize();
                            float pushStrength = damage * 0.3f * (1f - (float) ((dist - innerR) / (outerR - innerR)));
                            applyBlockImpulse(level, pos, pushDir.scale(pushStrength), Vec3.atCenterOf(pos));
                            blocksPushed++;
                        }
                    }
                }
            }
        }

        if (glueRemoved > 0 || blocksPushed > 0) {
            LOGGER.debug("Mid explosion: removed {} glue connections, pushed {} blocks", glueRemoved, blocksPushed);
        }
    }

    private static void handleOuterExplosion(Level level, Vec3 center, float innerR, float outerR, float damage) {
        BlockPos centerPos = BlockPos.containing(center);
        int r = (int) Math.ceil(outerR);
        int pushed = 0;

        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    BlockPos pos = centerPos.offset(x, y, z);
                    double dist = Math.sqrt(x * x + y * y + z * z);

                    if (dist > innerR && dist <= outerR) {
                        float chance = 0.15f * (1f - (float) ((dist - innerR) / (outerR - innerR)));
                        if (level.random.nextFloat() < chance) {
                            Vec3 pushDir = Vec3.atCenterOf(pos).subtract(center).normalize();
                            float pushStrength = damage * 0.1f;
                            applyBlockImpulse(level, pos, pushDir.scale(pushStrength), Vec3.atCenterOf(pos));
                            pushed++;
                        }
                    }
                }
            }
        }

        if (pushed > 0) {
            LOGGER.debug("Outer explosion: pushed {} blocks", pushed);
        }
    }

    private static void breakSubLevelBlocks(Level level, Set<BlockPos> positions, Vec3 center, float damage) {
        for (BlockPos pos : positions) {
            if (!SableCompat.isInSubLevel(level, pos)) continue;

            BlockState state = level.getBlockState(pos);
            BlockPos.MutableBlockPos mutablePos = pos.mutable();
            level.destroyBlock(mutablePos, true);
        }
    }

    private static void applyBlockImpulse(Level level, BlockPos pos, Vec3 impulse, Vec3 point) {
        if (!SableCompat.isInSubLevel(level, pos)) return;
        SableCompat.applyImpulseToSubLevelAt(level, pos, impulse, point);
    }

    private static void applyRadialImpulseToSubLevels(Level level, Vec3 center, float radius, float strength) {
        SableCompat.applyRadialImpulse(level, center, radius, strength * 10f);
    }

    private static void handleEntityDamage(Level level, Vec3 center, Entity owner,
                                            float radius, float damage,
                                            PhysicsProjectile.ProjectileType type) {
        AABB aabb = new AABB(
                center.x - radius, center.y - radius, center.z - radius,
                center.x + radius, center.y + radius, center.z + radius
        );

        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, aabb);
        for (LivingEntity entity : entities) {
            double dist = entity.position().distanceTo(center);
            if (dist > radius) continue;

            float damageFactor = 1f - (float) (dist / radius);
            float actualDamage = damage * damageFactor;

            if (actualDamage > 0.5f) {
                DamageSource source;
                if (owner != null) {
                    source = level.damageSources().explosion(owner);
                } else {
                    source = level.damageSources().explosion(null);
                }
                entity.hurt(source, actualDamage);
            }

            Vec3 knockback = entity.position().subtract(center).normalize().scale(damageFactor * 2);
            entity.push(knockback.x, knockback.y * 0.5, knockback.z);
        }
    }

    private static void spawnExplosionParticles(Level level, Vec3 center, PhysicsProjectile.ProjectileType type) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        switch (type) {
            case MOLOTOV -> {
                serverLevel.sendParticles(ParticleTypes.FLAME, center.x, center.y, center.z,
                        50, 0.5, 0.5, 0.5, 0.1);
                serverLevel.sendParticles(ParticleTypes.LAVA, center.x, center.y, center.z,
                        20, 0.3, 0.3, 0.3, 0.05);
            }
            case SMOKE_GRENADE -> {
                serverLevel.sendParticles(ParticleTypes.SMOKE, center.x, center.y, center.z,
                        100, 1, 1, 1, 0.02);
            }
            case FLASH_GRENADE, STUN_GRENADE -> {
                serverLevel.sendParticles(ParticleTypes.FLASH, center.x, center.y, center.z,
                        30, 0.1, 0.1, 0.1, 1);
            }
            default -> {
                serverLevel.sendParticles(ParticleTypes.EXPLOSION, center.x, center.y, center.z,
                        10, 0.5, 0.5, 0.5, 0.5);
                serverLevel.sendParticles(ParticleTypes.SMOKE, center.x, center.y, center.z,
                        30, 0.7, 0.7, 0.7, 0.1);
                serverLevel.sendParticles(ParticleTypes.FLAME, center.x, center.y, center.z,
                        20, 0.3, 0.3, 0.3, 0.05);
            }
        }
    }

    private static void playExplosionSound(Level level, Vec3 center, PhysicsProjectile.ProjectileType type) {
        float volume = switch (type) {
            case C4 -> 2.0f;
            case FRAG_GRENADE, GRENADE -> 1.5f;
            default -> 1.0f;
        };

        level.playSound(null, center.x, center.y, center.z,
                SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, volume,
                0.8f + level.random.nextFloat() * 0.4f);
    }
}
