package me.xjqsh.lesrainstactical.config;

import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class CommonConfig {
    public static ModConfigSpec.BooleanValue GRENADE_EXPLOSION_BLOCK_DAMAGE;
    public static ModConfigSpec.BooleanValue MELEE_ITEM_CONSUME_DURABILITY;
    public static ModConfigSpec.IntValue MELEE_IGNORE_INVULNERABLE_TICK_THRESHOLD;

    public static ModConfigSpec.BooleanValue ARMOR_PROTECTION_ENABLED;
    public static ModConfigSpec.DoubleValue ARMOR_DURABILITY_MULTIPLIER;

    public static ModConfigSpec init() {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder()
                .setConfigType(ModConfig.Type.COMMON);

        builder.push("grenade");
        GRENADE_EXPLOSION_BLOCK_DAMAGE = builder
                .comment("Whether grenade explosion can damage blocks")
                .define("grenadeExplosionBlockDamage", true);
        builder.pop();

        builder.push("melee");
        MELEE_ITEM_CONSUME_DURABILITY = builder
                .comment("Whether melee items consume durability on attack")
                .define("meleeItemConsumeDurability", true);
        MELEE_IGNORE_INVULNERABLE_TICK_THRESHOLD = builder
                .comment("When target's invulnerable tick is less than this value, melee attack will ignore it")
                .defineInRange("meleeIgnoreInvulnerableTickThreshold", 20, 0, 100);
        builder.pop();

        builder.push("armor");
        ARMOR_PROTECTION_ENABLED = builder
                .comment("Whether armor protection is enabled")
                .define("armorProtectionEnabled", true);
        ARMOR_DURABILITY_MULTIPLIER = builder
                .comment("Armor durability multiplier")
                .defineInRange("armorDurabilityMultiplier", 1.0, 0.1, 10.0);
        builder.pop();

        return builder.build();
    }
}