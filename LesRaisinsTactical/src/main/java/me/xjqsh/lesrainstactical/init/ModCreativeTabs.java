package me.xjqsh.lesrainstactical.init;

import me.xjqsh.lesrainstactical LesRaisinsTactical;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final String MOD_ID = LesRaisinsTactical.MOD_ID;

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final ResourceLocation ARMOR_ICON = new ResourceLocation(MOD_ID, "chemical_protective_chestplate");

    public static final RegistryObject<CreativeModeTab> ARMOR_TAB = TABS.register("armor",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.tab.lrarmor"))
                    .icon(() -> ForgeRegistries.ITEMS.getValue(ARMOR_ICON).getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        ModItems.ITEMS.getEntries().stream()
                                .map(RegistryObject::get)
                                .filter(item -> item instanceof me.xjqsh.lesraisinsarmor.item.LrArmorItem)
                                .map(Item::getDefaultInstance)
                                .forEach(output::accept);
                    })
                    .build()
    );
}
