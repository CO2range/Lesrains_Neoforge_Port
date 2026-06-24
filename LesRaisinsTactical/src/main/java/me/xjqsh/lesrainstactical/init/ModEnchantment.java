package me.xjqsh.lesrainstactical.init;

import me.xjqsh.lesrainstactical LesRaisinsTactical;
import me.xjqsh.lesrainstactical.enchantment.BackstabEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEnchantment {
    public static final String MOD_ID = LesRaisinsTactical.MOD_ID;

    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MOD_ID);

    public static final RegistryObject<Enchantment> BACKSTAB = ENCHANTMENTS.register("backstab", BackstabEnchantment::new);
}
