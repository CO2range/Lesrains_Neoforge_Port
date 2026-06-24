package me.xjqsh.lesrainstactical.compat.parkour;

import me.xjqsh.lesrainstactical.LesRaisinsTactical;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod.EventBusSubscriber(modid = ParkourCompat.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ParkourCompat {
    public static final String MOD_ID = "parkour";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static boolean isParkourLoaded = false;

    public ParkourCompat() {
        isParkourLoaded = NeoForge.MODS.isLoaded(MOD_ID);
        if (isParkourLoaded) {
            LOGGER.info("Parkour mod detected - enabling compatibility");
            initParkourBonuses();
        }
    }

    private void initParkourBonuses() {
        LOGGER.info("Initializing Parkour armor bonus system");
    }

    public static boolean isParkourLoaded() {
        return isParkourLoaded;
    }

    public static void registerParkourArmorSet(Object armorSet) {
        if (isParkourLoaded) {
            LOGGER.debug("Registering parkour armor set: {}", armorSet);
        }
    }

    public static float getParkourJumpBonus(Object armorItem) {
        if (isParkourLoaded) {
            return 0.1f;
        }
        return 0.0f;
    }

    public static float getParkourSpeedBonus(Object armorItem) {
        if (isParkourLoaded) {
            return 0.05f;
        }
        return 0.0f;
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        if (isParkourLoaded) {
            LOGGER.info("Parkour compatibility active - server starting");
        }
    }
}
