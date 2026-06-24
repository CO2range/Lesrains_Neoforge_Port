package me.xjqsh.lesrainstactical;

import me.xjqsh.lesrainstactical.config.CommonConfig;
import me.xjqsh.lesrainstactical.init.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(LesRaisinsTactical.MOD_ID)
public class LesRaisinsTactical {
    public static final String MOD_ID = "lesrainstactical";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public LesRaisinsTactical() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.register(this::commonSetup);

        // Register configs
        FMLJavaModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.init());

        // Register mod content
        ModItems.TABS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModEnchantment.ENCHANTMENTS.register(modEventBus);
        ModParticleTypes.PARTICLE_TYPES.register(modEventBus);
        ModEffects.EFFECTS.register(modEventBus);
        ModSounds.SOUNDS.register(modEventBus);

        // Armor registrations
        ModArmorMaterials.ARMOR_MATERIALS.register(modEventBus);

        // TACZ compatibility
        ModCreativeTabs.TABS.register(modEventBus);

        LOGGER.info("LesRaisinsTactical mod initialized");
    }

    private void commonSetup(final net.neoforged.fml.common.Mod LifecycleEvent) {
        LOGGER.info("LesRaisinsTactical common setup complete");
    }
}
