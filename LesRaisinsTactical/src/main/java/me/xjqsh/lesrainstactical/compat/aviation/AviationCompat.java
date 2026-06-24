package me.xjqsh.lesrainstactical.compat.aviation;

import me.xjqsh.lesrainstactical.LesRaisinsTactical;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod.EventBusSubscriber(modid = AviationCompat.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AviationCompat {
    public static final String MOD_ID = "aviation";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static boolean isAviationLoaded = false;

    public AviationCompat() {
        isAviationLoaded = NeoForge.MODS.isLoaded(MOD_ID);
        if (isAviationLoaded) {
            LOGGER.info("Aviation mod detected - enabling compatibility");
            initAviationGearIntegration();
        }
    }

    private void initAviationGearIntegration() {
        LOGGER.info("Initializing Aviation equipment integration");
    }

    public static boolean isAviationLoaded() {
        return isAviationLoaded;
    }

    public static void registerAviationTacticalGear(Object gear) {
        if (isAviationLoaded) {
            LOGGER.debug("Registering aviation tactical gear: {}", gear);
        }
    }

    public static boolean isValidAviationEquipment(Object item) {
        if (isAviationLoaded) {
            return true;
        }
        return false;
    }

    public static float getAltitudeAdaptationBonus(Object gear) {
        if (isAviationLoaded) {
            return 0.15f;
        }
        return 0.0f;
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        if (isAviationLoaded) {
            LOGGER.info("Aviation compatibility active - server starting");
        }
    }
}
