package me.xjqsh.lesrainstactical.compat.sable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class SableCompanionBridge {
    private static final Logger LOGGER = LoggerFactory.getLogger("LesRaisinsTactical:SableBridge");
    private static Object companionInstance;

    private static Method isInPlotGridMethod;
    private static Method projectOutOfSubLevelMethod;
    private static Method distanceSquaredWithSubLevelsMethod;
    private static Method getContainingMethod;
    private static Method logicalPoseMethod;
    private static Method transformPositionMethod;

    private static boolean bridgeReady = false;

    public static void init() {
        try {
            Class<?> companionClass = Class.forName("dev.ryanhcode.sable.companion.SableCompanion");
            companionInstance = companionClass.getField("INSTANCE").get(null);

            isInPlotGridMethod = companionClass.getMethod("isInPlotGrid", Level.class, BlockPos.class);
            projectOutOfSubLevelMethod = companionClass.getMethod("projectOutOfSubLevel", Level.class, Vec3.class);
            distanceSquaredWithSubLevelsMethod = companionClass.getMethod("distanceSquaredWithSubLevels", Level.class, Vec3.class, Vec3.class);
            getContainingMethod = companionClass.getMethod("getContaining", Level.class, BlockPos.class);

            Class<?> subLevelAccessClass = Class.forName("dev.ryanhcode.sable.companion.SubLevelAccess");
            logicalPoseMethod = subLevelAccessClass.getMethod("logicalPose");

            Class<?> poseClass = Class.forName("dev.ryanhcode.sable.companion.Pose3dc");
            transformPositionMethod = poseClass.getMethod("transformPosition", Vec3.class);

            bridgeReady = true;
            LOGGER.info("Sable Companion bridge initialized");
        } catch (Exception e) {
            LOGGER.error("Failed to initialize Sable Companion bridge", e);
        }
    }

    private static void checkBridge() {
        if (!bridgeReady) {
            throw new IllegalStateException("Sable Companion bridge not initialized");
        }
    }

    public static boolean isInPlotGrid(Level level, BlockPos pos) {
        checkBridge();
        try {
            return (boolean) isInPlotGridMethod.invoke(companionInstance, level, pos);
        } catch (Exception e) {
            LOGGER.error("Error in isInPlotGrid", e);
            return false;
        }
    }

    public static Vec3 projectOutOfSubLevel(Level level, Vec3 pos) {
        checkBridge();
        try {
            Object result = projectOutOfSubLevelMethod.invoke(companionInstance, level, pos);
            return (Vec3) result;
        } catch (Exception e) {
            LOGGER.error("Error in projectOutOfSubLevel", e);
            return pos;
        }
    }

    public static double distanceSquaredWithSubLevels(Level level, Vec3 a, Vec3 b) {
        checkBridge();
        try {
            return (double) distanceSquaredWithSubLevelsMethod.invoke(companionInstance, level, a, b);
        } catch (Exception e) {
            LOGGER.error("Error in distanceSquaredWithSubLevels", e);
            return a.distanceToSqr(b);
        }
    }

    public static Object getContaining(Level level, BlockPos pos) {
        checkBridge();
        try {
            return getContainingMethod.invoke(companionInstance, level, pos);
        } catch (Exception e) {
            LOGGER.error("Error in getContaining", e);
            return null;
        }
    }

    public static void applyImpulseToContaining(Level level, BlockPos pos, Vec3 impulse, Vec3 point) {
        Object subLevel = getContaining(level, pos);
        if (subLevel == null) return;

        try {
            Method applyImpulseMethod = subLevel.getClass().getMethod("applyImpulse", Vec3.class, Vec3.class);
            applyImpulseMethod.invoke(subLevel, impulse, point);
        } catch (Exception e) {
            LOGGER.trace("Could not apply impulse via companion (may not be supported)");
        }
    }

    public static void applyRadialImpulse(Level level, Vec3 center, float radius, float strength) {
        checkBridge();
        try {
            Method applyRadialImpulseMethod = companionInstance.getClass()
                    .getMethod("applyRadialImpulse", Level.class, Vec3.class, float.class, float.class);
            applyRadialImpulseMethod.invoke(companionInstance, level, center, radius, strength);
        } catch (Exception e) {
            LOGGER.trace("Radial impulse not available via companion");
        }
    }

    public static int breakBlocksInContaining(Level level, BlockPos centerPos, int radius) {
        Object subLevel = getContaining(level, centerPos);
        if (subLevel == null) return 0;

        try {
            Method breakBlocksMethod = subLevel.getClass()
                    .getMethod("breakBlocks", BlockPos.class, int.class);
            return (int) breakBlocksMethod.invoke(subLevel, centerPos, radius);
        } catch (Exception e) {
            LOGGER.trace("Block breaking not available via companion");
            return 0;
        }
    }

    public static float getContainingMass(Level level, BlockPos pos) {
        Object subLevel = getContaining(level, pos);
        if (subLevel == null) return 0f;

        try {
            Method getMassMethod = subLevel.getClass().getMethod("getMass");
            Object massTracker = getMassMethod.invoke(subLevel);
            if (massTracker != null) {
                Method getTotalMassMethod = massTracker.getClass().getMethod("getTotalMass");
                return ((Number) getTotalMassMethod.invoke(massTracker)).floatValue();
            }
        } catch (Exception e) {
            LOGGER.trace("Mass retrieval not available via companion");
        }
        return 0f;
    }
}
