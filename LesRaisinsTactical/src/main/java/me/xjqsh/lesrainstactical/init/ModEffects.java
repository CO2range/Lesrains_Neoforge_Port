package me.xjqsh.lesrainstactical.init;

import me.xjqsh.lesrainstactical LesRaisinsTactical;
import me.xjqsh.lesrainstactical.effect.SuitEffect;
import me.xjqsh.lesrainstactical.effect.BurnedEffect;
import me.xjqsh.lesrainstactical.effect.HarmfulEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    public static final String MOD_ID = LesRaisinsTactical.MOD_ID;

    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MOD_ID);

    // lrarmor effects
    public static final RegistryObject<MobEffect> TOUGH = EFFECTS.register("tough", () ->
            new SuitEffect(MobEffectCategory.BENEFICIAL, 0x000000, "attacker")
                    .addAttributeModifier(Attributes.ARMOR_TOUGHNESS, "62E0C0F3-D94E-3821-BF1C-B5F17CB7F971",
                            15, AttributeModifier.Operation.ADDITION)
                    .addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, "0BCE518E-9D05-CB4A-C435-C68BEEE57650",
                            0.25, AttributeModifier.Operation.ADDITION)
    );

    public static final RegistryObject<MobEffect> LIGHT_LEG = EFFECTS.register("light_leg", () ->
            new SuitEffect(MobEffectCategory.BENEFICIAL, 0x000000, "scout")
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED, "FC303D2A-7C7F-FFAB-2B4E-A5EF09BB2AF1",
                            0.025, AttributeModifier.Operation.ADDITION)
    );

    public static final RegistryObject<MobEffect> HEAVY_ARMOR = EFFECTS.register("heavy_armor", () ->
            new SuitEffect(MobEffectCategory.BENEFICIAL, 0x000000, "defender")
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED, "37DAE46A-CB52-2E2C-71FC-4AE7348B0B8D",
                            -0.02, AttributeModifier.Operation.ADDITION)
    );

    public static final RegistryObject<MobEffect> RESCUE = EFFECTS.register("rescue", () ->
            new SuitEffect(MobEffectCategory.BENEFICIAL, 0x000000, "medical")
    );

    public static final RegistryObject<MobEffect> RESCUE_COOLDOWN = EFFECTS.register("rescue_cooldown", () ->
            new MobEffect(MobEffectCategory.HARMFUL, 0x000000) {}
    );

    // lrtactical effects
    public static final RegistryObject<HarmfulEffect> BLIND = EFFECTS.register("blinded", () -> new HarmfulEffect(0xffffff));
    public static final RegistryObject<HarmfulEffect> DEAFENED = EFFECTS.register("deafened", () -> new HarmfulEffect(0xffffff));
    public static final RegistryObject<BurnedEffect> FLAMMABLE = EFFECTS.register("flammable", () -> new BurnedEffect(0xaa4727));
}
