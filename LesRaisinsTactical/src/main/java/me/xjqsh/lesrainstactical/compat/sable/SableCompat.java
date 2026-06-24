package me.xjqsh.lesrainstactical.compat.sable;

import me.xjqsh.lesrainstactical.LesRaisinsTactical;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod.EventBusSubscriber(modid = LesRaisinsTactical.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SableCompat {
    public static final Logger LOGGER = LoggerFactory.getLogger("LesRaisinsTactical:Sable");
    private static boolean initialized = false;

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            try {
                Class.forName("dev.ryanhcode.sable.api.Sable");
                initialized = true;
                LOGGER.info("Sable compatibility initialized successfully");
                NeoForge.EVENT_BUS.register(new SableEventHandler());
            } catch (ClassNotFoundException e) {
                LOGGER.error("Sable mod not found! LesRaisinsTactical requires Sable to function.");
                throw new RuntimeException("LesRaisinsTactical requires Sable mod to be installed.", e);
            }
        });
    }

    public static boolean isSableLoaded() {
        return initialized;
    }

    public static void requireSable() {
        if (!initialized) {
            throw new IllegalStateException("Sable is required but not initialized");
        }
    }

    public static boolean isInSubLevel(Level level, BlockPos pos) {
        requireSable();
        return SableCompanionBridge.isInPlotGrid(level, pos);
    }

    public static Vec3 projectOutOfSubLevel(Level level, Vec3 pos) {
        requireSable();
        return SableCompanionBridge.projectOutOfSubLevel(level, pos);
    }

    public static double distanceSquaredWithSubLevels(Level level, Vec3 a, Vec3 b) {
        requireSable();
        return SableCompanionBridge.distanceSquaredWithSubLevels(level, a, b);
    }

    public static void applyImpulseToSubLevelAt(Level level, BlockPos pos, Vec3 impulse, Vec3 point) {
        requireSable();
        SableCompanionBridge.applyImpulseToContaining(level, pos, impulse, point);
    }

    public static void applyRadialImpulse(Level level, Vec3 center, float radius, float strength) {
        requireSable();
        SableCompanionBridge.applyRadialImpulse(level, center, radius, strength);
    }

    public static int breakBlocksInSubLevel(Level level, BlockPos centerPos, int radius) {
        requireSable();
        return SableCompanionBridge.breakBlocksInContaining(level, centerPos, radius);
    }

    public static float getSubLevelMass(Level level, BlockPos pos) {
        requireSable();
        return SableCompanionBridge.getContainingMass(level, pos);
    }
}
