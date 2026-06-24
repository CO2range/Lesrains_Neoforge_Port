package me.xjqsh.lesrainstactical.compat.sable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class PhysicsProjectile {
    private static final Logger LOGGER = LoggerFactory.getLogger("LesRaisinsTactical:PhysicsProjectile");

    private final Level level;
    private UUID subLevelId;
    private Vec3 position;
    private Vec3 velocity;
    private float mass;
    private float dragCoefficient = 0.99f;
    private boolean isAlive = true;
    private int tickCount = 0;
    private int maxLife = 200;
    private ProjectileType projectileType;
    private LivingEntity owner;
    private float explosionRadius;
    private float explosionDamage;
    private boolean destroysBlocks;
    private BlockState visualBlock;

    public enum ProjectileType {
        GRENADE,
        MOLOTOV,
        FLASH_GRENADE,
        SMOKE_GRENADE,
        C4,
        FRAG_GRENADE,
        STUN_GRENADE
    }

    public PhysicsProjectile(Level level, LivingEntity owner, ProjectileType type, Vec3 pos, Vec3 vel) {
        this.level = level;
        this.owner = owner;
        this.projectileType = type;
        this.position = pos;
        this.velocity = vel;
        this.mass = getProjectileMass(type);
        this.visualBlock = getVisualBlock(type);
        this.explosionRadius = getExplosionRadius(type);
        this.explosionDamage = getExplosionDamage(type);
        this.destroysBlocks = getDestroysBlocks(type);
        this.maxLife = getMaxLife(type);
    }

    private float getProjectileMass(ProjectileType type) {
        return switch (type) {
            case GRENADE, FRAG_GRENADE -> 0.4f;
            case MOLOTOV -> 0.5f;
            case FLASH_GRENADE, STUN_GRENADE -> 0.3f;
            case SMOKE_GRENADE -> 0.35f;
            case C4 -> 1.5f;
        };
    }

    private BlockState getVisualBlock(ProjectileType type) {
        return switch (type) {
            case MOLOTOV -> Blocks.HONEY_BLOCK.defaultBlockState();
            case SMOKE_GRENADE -> Blocks.GRAY_WOOL.defaultBlockState();
            case FLASH_GRENADE -> Blocks.QUARTZ_BLOCK.defaultBlockState();
            default -> Blocks.IRON_BLOCK.defaultBlockState();
        };
    }

    private float getExplosionRadius(ProjectileType type) {
        return switch (type) {
            case GRENADE -> 4.5f;
            case FRAG_GRENADE -> 6.0f;
            case MOLOTOV -> 3.0f;
            case FLASH_GRENADE, STUN_GRENADE -> 8.0f;
            case SMOKE_GRENADE -> 5.0f;
            case C4 -> 10.0f;
        };
    }

    private float getExplosionDamage(ProjectileType type) {
        return switch (type) {
            case GRENADE -> 18.0f;
            case FRAG_GRENADE -> 25.0f;
            case MOLOTOV -> 6.0f;
            case FLASH_GRENADE, STUN_GRENADE -> 2.0f;
            case SMOKE_GRENADE -> 0.0f;
            case C4 -> 50.0f;
        };
    }

    private boolean getDestroysBlocks(ProjectileType type) {
        return switch (type) {
            case GRENADE, FRAG_GRENADE, C4 -> true;
            default -> false;
        };
    }

    private int getMaxLife(ProjectileType type) {
        return switch (type) {
            case GRENADE, FRAG_GRENADE -> 100;
            case MOLOTOV -> 80;
            case FLASH_GRENADE, STUN_GRENADE -> 60;
            case SMOKE_GRENADE -> 180;
            case C4 -> Integer.MAX_VALUE;
        };
    }

    public void spawn() {
        if (level.isClientSide()) return;

        try {
            this.subLevelId = createPhysicsSubLevel();
            if (subLevelId != null) {
                setVelocity(velocity);
                SableProjectileManager.registerProjectile(this);
                LOGGER.debug("Spawned physics projectile {} at {}", projectileType, position);
            } else {
                LOGGER.warn("Failed to create physics sub-level for projectile");
            }
        } catch (Exception e) {
            LOGGER.error("Error spawning physics projectile", e);
        }
    }

    private UUID createPhysicsSubLevel() {
        try {
            Class<?> subLevelClass = Class.forName("dev.ryanhcode.sable.server.ServerSubLevel");
            Method assembleMethod = subLevelClass.getMethod("assemble",
                    Level.class, BlockPos.class, BlockPos.class, BlockPos.class);

            BlockPos origin = BlockPos.containing(position);
            BlockPos min = origin.offset(-1, -1, -1);
            BlockPos max = origin.offset(1, 1, 1);
            BlockPos pivot = origin;

            Object subLevel = assembleMethod.invoke(null, level, min, max, pivot);
            if (subLevel == null) return null;

            Method getIdMethod = subLevel.getClass().getMethod("getId");
            UUID id = (UUID) getIdMethod.invoke(subLevel);

            try {
                Method setMassMethod = subLevel.getClass().getMethod("setMass", float.class);
                setMassMethod.invoke(subLevel, mass);
            } catch (Exception e) {
                LOGGER.trace("Could not set mass directly");
            }

            try {
                Method setDynamicMethod = subLevel.getClass().getMethod("setDynamic", boolean.class);
                setDynamicMethod.invoke(subLevel, true);
            } catch (Exception e) {
                LOGGER.trace("Could not set dynamic state");
            }

            return id;
        } catch (Exception e) {
            LOGGER.error("Failed to create physics sub-level via reflection", e);
            return null;
        }
    }

    public void setVelocity(Vec3 vel) {
        this.velocity = vel;
        if (subLevelId == null) return;

        try {
            Object subLevel = getSubLevelById();
            if (subLevel == null) return;

            Method applyImpulseMethod = subLevel.getClass().getMethod("applyLinearImpulse", Vec3.class);
            applyImpulseMethod.invoke(subLevel, vel.scale(mass));
        } catch (Exception e) {
            LOGGER.trace("Could not set velocity on sub-level");
        }
    }

    public Object getSubLevelById() {
        try {
            Class<?> sableClass = Class.forName("dev.ryanhcode.sable.api.Sable");
            Method getServerMethod = sableClass.getMethod("getServer", Level.class);
            Object sableServer = getServerMethod.invoke(null, level);

            Method getSubLevelMethod = sableServer.getClass().getMethod("getSubLevel", UUID.class);
            return getSubLevelMethod.invoke(sableServer, subLevelId);
        } catch (Exception e) {
            LOGGER.trace("Could not get sub-level by id");
            return null;
        }
    }

    public void tick() {
        tickCount++;
        if (tickCount > maxLife) {
            detonate();
            return;
        }

        Object subLevel = getSubLevelById();
        if (subLevel == null) {
            isAlive = false;
            return;
        }

        try {
            Method getPoseMethod = subLevel.getClass().getMethod("getLogicalPose");
            Object pose = getPoseMethod.invoke(subLevel);

            Method getTranslationMethod = pose.getClass().getMethod("getTranslation");
            Vec3 translation = (Vec3) getTranslationMethod.invoke(pose);
            this.position = translation;

            Method getLinearVelocityMethod = subLevel.getClass().getMethod("getLinearVelocity");
            this.velocity = (Vec3) getLinearVelocityMethod.invoke(subLevel);

            if (velocity.length() < 0.05 && tickCount > 20) {
                if (projectileType != ProjectileType.C4) {
                    detonate();
                }
            }
        } catch (Exception e) {
            LOGGER.trace("Error ticking physics projectile", e);
        }
    }

    public void detonate() {
        if (!isAlive) return;
        isAlive = false;

        if (level.isClientSide()) return;

        LOGGER.debug("Detonating {} at {}", projectileType, position);

        try {
            SableExplosionHandler.handleExplosion(
                    level, position, owner,
                    explosionRadius, explosionDamage,
                    destroysBlocks, projectileType
            );

            Object subLevel = getSubLevelById();
            if (subLevel != null) {
                try {
                    Method disassembleMethod = subLevel.getClass().getMethod("disassemble");
                    disassembleMethod.invoke(subLevel);
                } catch (Exception e) {
                    LOGGER.trace("Could not disassemble projectile sub-level");
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error during detonation", e);
        }

        SableProjectileManager.unregisterProjectile(this);
    }

    public boolean isAlive() {
        return isAlive;
    }

    public Vec3 getPosition() {
        return position;
    }

    public UUID getSubLevelId() {
        return subLevelId;
    }

    public ProjectileType getType() {
        return projectileType;
    }

    public Level getLevel() {
        return level;
    }

    public LivingEntity getOwner() {
        return owner;
    }

    public float getMass() {
        return mass;
    }

    public int getTickCount() {
        return tickCount;
    }

    public void save(CompoundTag tag) {
        tag.putUUID("SubLevelId", subLevelId);
        tag.putString("Type", projectileType.name());
        tag.putInt("TickCount", tickCount);
        tag.putDouble("PosX", position.x);
        tag.putDouble("PosY", position.y);
        tag.putDouble("PosZ", position.z);
    }

    public static PhysicsProjectile load(Level level, CompoundTag tag) {
        UUID id = tag.getUUID("SubLevelId");
        ProjectileType type = ProjectileType.valueOf(tag.getString("Type"));
        Vec3 pos = new Vec3(tag.getDouble("PosX"), tag.getDouble("PosY"), tag.getDouble("PosZ"));

        PhysicsProjectile proj = new PhysicsProjectile(level, null, type, pos, Vec3.ZERO);
        proj.subLevelId = id;
        proj.tickCount = tag.getInt("TickCount");
        return proj;
    }
}
