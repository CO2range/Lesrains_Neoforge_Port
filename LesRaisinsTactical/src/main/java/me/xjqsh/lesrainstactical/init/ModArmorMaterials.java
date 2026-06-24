package me.xjqsh.lesrainstactical.init;

import me.xjqsh.lesrainstactical LesRaisinsTactical;
import me.xjqsh.lesrainstactical.armor.LrArmorMaterial;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModArmorMaterials {
    public static final String MOD_ID = LesRaisinsTactical.MOD_ID;

    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(ForgeRegistries.ARMOR_MATERIALS, MOD_ID);

    private static final int[] HEALTH_PER_SLOT = new int[]{13, 15, 16, 11};

    public static final RegistryObject<LrArmorMaterial> DEFAULT = ARMOR_MATERIALS.register("default",
            () -> new LrArmorMaterial("dea", 50, new int[]{1, 2, 3, 1}, 15, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F,
                    () -> Ingredient.of(Items.LEATHER))
    );

    public static final RegistryObject<LrArmorMaterial> IRON = ARMOR_MATERIALS.register("iron",
            () -> new LrArmorMaterial("iron", 33, new int[]{2, 5, 6, 2}, 9, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F,
                    () -> Ingredient.of(Items.IRON_INGOT))
    );

    public static final RegistryObject<LrArmorMaterial> DIAMOND = ARMOR_MATERIALS.register("diamond",
            () -> new LrArmorMaterial("diamond", 50, new int[]{3, 6, 8, 3}, 10, SoundEvents.ARMOR_EQUIP_DIAMOND, 2.0F, 0.0F,
                    () -> Ingredient.of(Items.DIAMOND))
    );

    public static final RegistryObject<LrArmorMaterial> NETHERITE = ARMOR_MATERIALS.register("netherite",
            () -> new LrArmorMaterial("netherite", 66, new int[]{3, 6, 8, 3}, 15, SoundEvents.ARMOR_EQUIP_NETHERITE, 3.0F, 1.0F,
                    () -> Ingredient.of(Items.NETHERITE_INGOT))
    );
}
