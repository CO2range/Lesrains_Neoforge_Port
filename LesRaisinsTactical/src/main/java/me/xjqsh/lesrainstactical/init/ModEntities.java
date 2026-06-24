package me.xjqsh.lesrainstactical.init;

import me.xjqsh.lesrainstactical LesRaisinsTactical;
import me.xjqsh.lesrainstactical.entity.*;
import me.xjqsh.lesrainstactical.entity.sp.SpEffectCloudEntity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegistryObject;

public class ModEntities {
    public static final String MOD_ID = LesRaisinsTactical.MOD_ID;

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(NeoForgeRegistries.ENTITY_TYPES, MOD_ID);

    public static final RegistryObject<EntityType<GrenadeEntity>> GRENADE = ENTITY_TYPES.register("explode_grenade",
            () -> GrenadeEntity.TYPE);

    public static final RegistryObject<EntityType<SmokeGrenadeEntity>> SMOKE_GRENADE = ENTITY_TYPES.register("smoke_grenade",
            () -> SmokeGrenadeEntity.TYPE);

    public static final RegistryObject<EntityType<StunGrenadeEntity>> STUN_GRENADE = ENTITY_TYPES.register("stun_grenade",
            () -> StunGrenadeEntity.TYPE);

    public static final RegistryObject<EntityType<FlashGrenadeEntity>> FLASH_GRENADE = ENTITY_TYPES.register("flash_grenade",
            () -> FlashGrenadeEntity.TYPE);

    public static final RegistryObject<EntityType<C4Entity>> C4 = ENTITY_TYPES.register("c4",
            () -> C4Entity.TYPE);

    public static final RegistryObject<EntityType<MolotovEntity>> MOLOTOV = ENTITY_TYPES.register("molotov",
            () -> MolotovEntity.TYPE);

    public static final RegistryObject<EntityType<SpEffectCloudEntity>> EFFECT_CLOUD = ENTITY_TYPES.register("sp_effect_cloud",
            () -> SpEffectCloudEntity.TYPE);
}
