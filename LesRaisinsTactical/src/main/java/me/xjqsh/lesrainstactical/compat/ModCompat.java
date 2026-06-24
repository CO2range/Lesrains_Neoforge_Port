package me.xjqsh.lesrainstactical.compat;

import me.xjqsh.lesrainstactical.LesRaisinsTactical;
import me.xjqsh.lesrainstactical.compat.aviation.AviationCompat;
import me.xjqsh.lesrainstactical.compat.create.CreateCompat;
import me.xjqsh.lesrainstactical.compat.parkour.ParkourCompat;
import me.xjqsh.lesrainstactical.compat.tacz.TaczCompat;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(LesRaisinsTactical.MOD_ID)
public class ModCompat {
    public static final String MOD_ID = "lesrainstactical";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public ModCompat() {
        LOGGER.info("Initializing compatibility layer");
        loadAllCompat();
    }

    private void loadAllCompat() {
        LOGGER.info("Loading all mod compatibility modules");

        if (TaczCompat.isTaczLoaded()) {
            LOGGER.info("TACZ compatibility module loaded");
        }

        if (CreateCompat.isCreateLoaded()) {
            LOGGER.info("Create compatibility module loaded");
        }

        if (ParkourCompat.isParkourLoaded()) {
            LOGGER.info("Parkour compatibility module loaded");
        }

        if (AviationCompat.isAviationLoaded()) {
            LOGGER.info("Aviation compatibility module loaded");
        }

        LOGGER.info("Compatibility layer initialization complete");
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("ModCompat: Server starting - compatibility modules active");
    }
}
