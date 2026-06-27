package me.xjqsh.lesrainstactical.compat.sable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod.EventBusSubscriber(modid = "lesrainstactical")
public class SableCompat {
    public static final Logger LOGGER = LoggerFactory.getLogger("LesRaisinsTactical:Sable");
    private static boolean initialized = false;

    @SubscribeEvent
    public static void onCommonSetup(net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent event) {
        try {
            Class.forName("dev.ryanhcode.sable.api.Sable");
            initialized = true;
            LOGGER.info("Sable compatibility module loaded");
        } catch (ClassNotFoundException e) {
            LOGGER.info("Sable mod not found - physics features disabled");
        }
    }

    public static boolean isSableLoaded() {
        return initialized;
    }

    public static boolean isInSubLevel(Level level, BlockPos pos) {
        if (!initialized) return false;
        try {
            Class<?> companionClass = Class.forName("dev.ryanhcode.sable.companion.SableCompanion");
            Object instance = companionClass.getField("INSTANCE").get(null);
            var method = companionClass.getMethod("isInPlotGrid", Level.class, BlockPos.class);
            return (boolean) method.invoke(instance, level, pos);
        } catch (Exception e) {
            return false;
        }
    }

    public static Object getContainingSubLevel(Level level, BlockPos pos) {
        if (!initialized) return null;
        try {
            Class<?> companionClass = Class.forName("dev.ryanhcode.sable.companion.SableCompanion");
            Object instance = companionClass.getField("INSTANCE").get(null);
            var method = companionClass.getMethod("getContaining", Level.class, BlockPos.class);
            return method.invoke(instance, level, pos);
        } catch (Exception e) {
            return null;
        }
    }

    public static void applyImpulseToSubLevel(Object subLevelAccess, Vec3 impulse, Vec3 point) {
        if (subLevelAccess == null) return;
        try {
            var method = subLevelAccess.getClass().getMethod("applyImpulse", Vec3.class, Vec3.class);
            method.invoke(subLevelAccess, impulse, point);
        } catch (Exception e) {
            LOGGER.trace("Could not apply impulse to sub-level");
        }
    }

    public static Vec3 projectOutOfSubLevel(Level level, Vec3 pos) {
        if (!initialized) return pos;
        try {
            Class<?> companionClass = Class.forName("dev.ryanhcode.sable.companion.SableCompanion");
            Object instance = companionClass.getField("INSTANCE").get(null);
            var method = companionClass.getMethod("projectOutOfSubLevel", Level.class, Vec3.class);
            return (Vec3) method.invoke(instance, level, pos);
        } catch (Exception e) {
            return pos;
        }
    }
}
