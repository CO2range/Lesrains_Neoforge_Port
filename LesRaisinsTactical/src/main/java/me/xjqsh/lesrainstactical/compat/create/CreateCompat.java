package me.xjqsh.lesrainstactical.compat.create;

import me.xjqsh.lesrainstactical.LesRaisinsTactical;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod.EventBusSubscriber(modid = CreateCompat.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CreateCompat {
    public static final String MOD_ID = "create";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static boolean isCreateLoaded = false;

    public CreateCompat() {
        isCreateLoaded = NeoForge.MODS.isLoaded(MOD_ID);
        if (isCreateLoaded) {
            LOGGER.info("Create mod detected - enabling compatibility");
            initMechanicalIntegration();
        }
    }

    private void initMechanicalIntegration() {
        LOGGER.info("Initializing Create mechanical systems integration");
    }

    public static boolean isCreateLoaded() {
        return isCreateLoaded;
    }

    public static void registerMechanicalTacticalItem(Object item) {
        if (isCreateLoaded) {
            LOGGER.debug("Registering tactical item with Create mechanical systems: {}", item);
        }
    }

    public static void registerRotaryAction(Object action) {
        if (isCreateLoaded) {
            LOGGER.debug("Registering rotary action: {}", action);
        }
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        if (isCreateLoaded) {
            LOGGER.info("Create compatibility active - server starting");
        }
    }
}
