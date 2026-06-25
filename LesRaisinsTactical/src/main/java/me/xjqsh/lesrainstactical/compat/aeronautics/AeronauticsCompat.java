package me.xjqsh.lesrainstactical.compat.aeronautics;

import me.xjqsh.lesrainstactical.init.ModItems;
import me.xjqsh.lesrainstactical.item.MeleeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mod.EventBusSubscriber(modid = "lesrainstactical")
public class AeronauticsCompat {
    public static final Logger LOGGER = LoggerFactory.getLogger("LesRaisinsTactical:Aeronautics");
    public static final String MOD_ID = "lesrainstactical";

    @SubscribeEvent
    public static void onMeleeAttack(AttackEntityEvent event) {
        Entity target = event.getTarget();
        Player attacker = event.getPlayer();
        Level level = attacker.level();

        if (level.isClientSide()) return;

        ItemStack heldItem = attacker.getItemInHand(InteractionHand.MAIN_HAND);
        if (!(heldItem.getItem() instanceof MeleeItem meleeItem)) return;

        if (isSableLoaded()) {
            handleAeronauticsMeleePush(level, target, attacker, meleeItem);
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        BlockPos pos = event.getPos();
        Level level = event.getLevel();

        if (level.isClientSide()) return;
        if (!isSableLoaded()) return;

        if (isInSubLevel(level, pos)) {
            BlockState state = level.getBlockState(pos);
            LOGGER.debug("Breaking block {} at {} in sub-level", state.getBlock().getName().getString(), pos);
        }
    }

    @SubscribeEvent
    public static void onExplosionDetonate(ExplosionEvent.Detonate event) {
        Level level = event.getLevel();
        if (level.isClientSide()) return;
        if (!isSableLoaded()) return;

        Set<BlockPos> affectedBlocks = event.getExplosion().getToBlow();
        Vec3 center = Vec3.atCenterOf(event.getExplosion().getPosition());

        LOGGER.debug("Explosion at {} affecting {} blocks", center, affectedBlocks.size());

        for (BlockPos pos : affectedBlocks) {
            if (isInSubLevel(level, pos)) {
                LOGGER.debug("Block at {} is in sub-level - applying physics effects", pos);
                applyExplosionPhysicsToSubLevel(level, pos, center, event.getExplosion().getRadius());
            }
        }
    }

    private static void handleAeronauticsMeleePush(Level level, Entity target, LivingEntity attacker, MeleeItem meleeItem) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        Vec3 attackPos = attacker.position();
        Vec3 targetPos = target.position();
        Vec3 direction = targetPos.subtract(attackPos).normalize();

        float basePushForce = meleeItem.getPushForce();
        float chargeMultiplier = 1.0f;
        if (attacker.isShiftKeyDown()) {
            chargeMultiplier = 2.0f;
        }

        float finalPushForce = basePushForce * chargeMultiplier;

        Vec3 pushVector = direction.scale(finalPushForce);
        pushVector = new Vec3(pushVector.x, pushVector.y * 0.5 + 0.25, pushVector.z);

        BlockPos targetBlockPos = BlockPos.containing(targetPos);
        if (isInSubLevel(level, targetBlockPos)) {
            applyPushToSubLevel(serverLevel, targetBlockPos, pushVector, targetPos);
            spawnPushParticles(serverLevel, targetPos);
            LOGGER.debug("Applied melee push of {} to sub-level at {}", pushVector, targetBlockPos);
        }

        if (target instanceof LivingEntity livingTarget) {
            target.push(pushVector.x, pushVector.y * 0.3, pushVector.z);
        }
    }

    public static void applyExplosionPhysicsToSubLevel(Level level, BlockPos subLevelBlockPos, Vec3 explosionCenter, float radius) {
        if (!isSableLoaded()) return;

        try {
            Object subLevelAccess = getContainingSubLevel(level, subLevelBlockPos);
            if (subLevelAccess == null) return;

            Vec3 blockCenter = Vec3.atCenterOf(subLevelBlockPos);
            Vec3 direction = blockCenter.subtract(explosionCenter).normalize();
            float distance = (float) blockCenter.distanceTo(explosionCenter);
            float falloff = 1.0f - Math.min(distance / radius, 1.0f);

            float explosionStrength = radius * 15.0f * falloff;
            Vec3 impulse = direction.scale(explosionStrength);

            applyImpulseToSubLevel(subLevelAccess, impulse, blockCenter);

            LOGGER.debug("Applied explosion impulse {} to sub-level containing {}", impulse, subLevelBlockPos);
        } catch (Exception e) {
            LOGGER.trace("Could not apply explosion physics to sub-level", e);
        }
    }

    private static void applyPushToSubLevel(ServerLevel level, BlockPos pos, Vec3 pushVector, Vec3 contactPoint) {
        if (!isSableLoaded()) return;

        try {
            Object subLevelAccess = getContainingSubLevel(level, pos);
            if (subLevelAccess == null) return;

            applyImpulseToSubLevel(subLevelAccess, pushVector, contactPoint);
        } catch (Exception e) {
            LOGGER.trace("Could not apply push to sub-level", e);
        }
    }

    private static void spawnPushParticles(ServerLevel level, Vec3 pos) {
        level.sendParticles(ParticleTypes.CRIT,
                pos.x, pos.y + 0.5, pos.z,
                8, 0.3, 0.3, 0.3, 0.1);
    }

    private static boolean isSableLoaded() {
        try {
            Class.forName("dev.ryanhcode.sable.api.Sable");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static boolean isInSubLevel(Level level, BlockPos pos) {
        if (!isSableLoaded()) return false;
        try {
            Class<?> companionClass = Class.forName("dev.ryanhcode.sable.companion.SableCompanion");
            Object instance = companionClass.getField("INSTANCE").get(null);
            java.lang.reflect.Method method = companionClass.getMethod("isInPlotGrid", Level.class, BlockPos.class);
            return (boolean) method.invoke(instance, level, pos);
        } catch (Exception e) {
            return false;
        }
    }

    private static Object getContainingSubLevel(Level level, BlockPos pos) {
        if (!isSableLoaded()) return null;
        try {
            Class<?> companionClass = Class.forName("dev.ryanhcode.sable.companion.SableCompanion");
            Object instance = companionClass.getField("INSTANCE").get(null);
            java.lang.reflect.Method method = companionClass.getMethod("getContaining", Level.class, BlockPos.class);
            return method.invoke(instance, level, pos);
        } catch (Exception e) {
            LOGGER.trace("Could not get containing sub-level", e);
            return null;
        }
    }

    private static void applyImpulseToSubLevel(Object subLevelAccess, Vec3 impulse, Vec3 point) {
        try {
            java.lang.reflect.Method method = subLevelAccess.getClass().getMethod("applyImpulse", Vec3.class, Vec3.class);
            method.invoke(subLevelAccess, impulse, point);
        } catch (Exception e) {
            LOGGER.trace("Could not apply impulse to sub-level", e);
        }
    }
}
