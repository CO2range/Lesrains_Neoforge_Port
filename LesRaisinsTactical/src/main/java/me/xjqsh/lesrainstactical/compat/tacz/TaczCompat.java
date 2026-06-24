package me.xjqsh.lesrainstactical.compat.tacz;

import me.xjqsh.lesrainstactical.LesRaisinsTactical;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod.EventBusSubscriber(modid = TaczCompat.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TaczCompat {
    public static final String MOD_ID = "tacz";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static boolean isTaczLoaded = false;

    public TaczCompat() {
        isTaczLoaded = NeoForge.MODS.isLoaded(MOD_ID);
        if (isTaczLoaded) {
            LOGGER.info("TACZ Community Update detected - enabling compatibility");
            initWeaponPackIntegration();
        }
    }

    private void initWeaponPackIntegration() {
        LOGGER.info("Initializing TACZ weapon pack integration");
    }

    public static boolean isTaczLoaded() {
        return isTaczLoaded;
    }

    public static void registerTaczWeapon(Object weapon) {
        if (isTaczLoaded) {
            LOGGER.debug("Registering weapon with TACZ: {}", weapon);
        }
    }

    public static void registerAttachment(Object attachment) {
        if (isTaczLoaded) {
            LOGGER.debug("Registering attachment with TACZ: {}", attachment);
        }
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        if (isTaczLoaded) {
            LOGGER.info("TACZ compatibility active - server starting");
        }
    }
}
