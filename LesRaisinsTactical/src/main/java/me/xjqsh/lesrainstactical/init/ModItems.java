package me.xjqsh.lesrainstactical.init;

import me.xjqsh.lesrainstactical LesRaisinsTactical;
import me.xjqsh.lesrainstactical.api.item.IConsumable;
import me.xjqsh.lesrainstactical.api.item.IMeleeWeapon;
import me.xjqsh.lesrainstactical.api.item.IThrowable;
import me.xjqsh.lesrainstactical.api.LrTacticalAPI;
import me.xjqsh.lesrainstactical.item.*;
import me.xjqsh.lesrainstactical.item.index.ConsumableIndex;
import me.xjqsh.lesrainstactical.item.index.MeleeWeaponIndex;
import me.xjqsh.lesrainstactical.item.index.ThrowableIndex;
import me.xjqsh.lesraisinsarmor.item.LrArmorItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final String MOD_ID = LesRaisinsTactical.MOD_ID;

    // Creative Mode Tabs
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final RegistryObject<CreativeModeTab> THROWABLE_TAB = TABS.register("throwable",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("item_group.lrtactical.throwable"))
                    .icon(ModItems::getThrowableIcon)
                    .displayItems(ModItems::fillThrowables)
                    .build()
    );

    public static final RegistryObject<CreativeModeTab> CONSUMABLE_TAB = TABS.register("consumable",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("item_group.lrtactical.consumable"))
                    .icon(ModItems::getConsumableIcon)
                    .displayItems(ModItems::fillConsumables)
                    .withTabsBefore(THROWABLE_TAB.getId())
                    .build()
    );

    public static final RegistryObject<CreativeModeTab> MELEE_TAB = TABS.register("melee",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("item_group.lrtactical.melee"))
                    .icon(ModItems::getMeleeIcon)
                    .displayItems(ModItems::fillMeleeWeapons)
                    .withTabsBefore(CONSUMABLE_TAB.getId())
                    .build()
    );

    // Item Registry
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    // lrtactical items
    public static final RegistryObject<ConsumableItem> CONSUMABLE = ITEMS.register("consumable", ConsumableItem::new);
    public static final RegistryObject<ThrowableItem> THROWABLE = ITEMS.register("throwable", ThrowableItem::new);
    public static final RegistryObject<MeleeItem> MELEE = ITEMS.register("melee", MeleeItem::new);
    public static final RegistryObject<FlashShieldItem> FLASH_SHIELD = ITEMS.register("flash_shield", FlashShieldItem::new);
    public static final RegistryObject<DetonatorItem> DETONATOR = ITEMS.register("detonator", DetonatorItem::new);

    // lrarmor items - register all armor sets
    // Note: Effect suppliers reference local ModEffects from merged mod
    static {
        registerArmorInBatch("armored_chemical", null);
        registerArmorInBatch("attacker", ModEffects.TOUGH::get);
        registerArmorInBatch("chemical_protective", null);
        registerArmorInBatch("defender", ModEffects.HEAVY_ARMOR::get);
        registerArmorInBatch("medical", ModEffects.RESCUE::get);
        registerArmorInBatch("scout", ModEffects.LIGHT_LEG::get);
        registerArmorInBatch("sniper", null);
        registerArmorInBatch("dea_armed", null);
        registerArmorInBatch("dea", null);
        registerArmorInBatch("atf", null);
        registerArmorInBatch("atf_vest", null);
        registerArmorInBatch("irs", null);
        registerArmorInBatch("fbi", null);
        registerArmorInBatch("fbi_armed", null);
        registerArmorInBatch("joker", null);
        registerArmorSingle(ArmorItem.Type.HELMET, "joker_armed");
        registerArmorSingle(ArmorItem.Type.CHESTPLATE, "joker_armed");
    }

    private static void registerArmorSingle(ArmorItem.Type slotType, String name) {
        String slotName = slotType.getName();
        ITEMS.register(name + "_" + slotName,
                () -> new LrArmorItem(name, slotType, new Item.Properties(), null));
    }

    private static void registerArmorInBatch(String name, java.util.function.Supplier<net.minecraft.world.effect.MobEffect> effectSupplier) {
        for (ArmorItem.Type slotType : ArmorItem.Type.values()) {
            String slotName = slotType.getName();
            ITEMS.register(name + "_" + slotName,
                    () -> new LrArmorItem(name, slotType, new Item.Properties(), effectSupplier));
        }
    }

    public static ItemStack getThrowableIcon() {
        ItemStack stack = new ItemStack(THROWABLE.get());
        IThrowable iThrowable = IThrowable.of(stack);
        if (iThrowable != null) {
            iThrowable.setId(stack, new ResourceLocation(MOD_ID, "m67"));
        }
        return stack;
    }

    public static ItemStack getConsumableIcon() {
        ItemStack stack = new ItemStack(CONSUMABLE.get());
        IConsumable consumable = IConsumable.of(stack);
        if (consumable != null) {
            consumable.setId(stack, new ResourceLocation(MOD_ID, "blood_pack"));
        }
        return stack;
    }

    public static ItemStack getMeleeIcon() {
        ItemStack stack = new ItemStack(MELEE.get());
        IMeleeWeapon iMeleeWeapon = IMeleeWeapon.of(stack);
        if (iMeleeWeapon != null) {
            iMeleeWeapon.setId(stack, new ResourceLocation(MOD_ID, "karambit"));
        }
        return stack;
    }

    public static void fillConsumables(CreativeModeTab.ItemDisplayParameters pParameters, CreativeModeTab.Output pOutput) {
        for (ConsumableIndex index : LrTacticalAPI.getConsumableIndexes()) {
            ItemStack stack = index.createItemStack();
            pOutput.accept(stack);
        }
    }

    public static void fillThrowables(CreativeModeTab.ItemDisplayParameters pParameters, CreativeModeTab.Output pOutput) {
        for (ThrowableIndex<?, ?> index : LrTacticalAPI.getThrowableIndexes()) {
            ItemStack stack = index.createItemStack();
            pOutput.accept(stack);
        }
        pOutput.accept(new ItemStack(DETONATOR.get()));
    }

    public static void fillMeleeWeapons(CreativeModeTab.ItemDisplayParameters pParameters, CreativeModeTab.Output pOutput) {
        for (MeleeWeaponIndex<?> index : LrTacticalAPI.getMeleeIndexes()) {
            ItemStack stack = index.createItemStack();
            pOutput.accept(stack);
        }
        pOutput.accept(new ItemStack(FLASH_SHIELD.get()));
    }
}
