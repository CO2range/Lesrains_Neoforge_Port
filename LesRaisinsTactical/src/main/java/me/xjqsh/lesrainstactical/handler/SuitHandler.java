package me.xjqsh.lesrainstactical.handler;

import me.xjqsh.lesrainstactical.init.ModEffects;
import me.xjqsh.lesrainstactical.item.LrArmorItem;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = "lesrainstactical", bus = EventBusSubscriber.Bus.GAME)
public class SuitHandler {

    @SubscribeEvent
    public static void onPlayerTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        
        handleSuitSetBonuses(player);
    }

    private static void handleSuitSetBonuses(Player player) {
        for (String suitId : getUniqueSuitIds(player)) {
            int suitCount = LrArmorItem.getSuitCount(player, suitId);
            
            if (suitCount >= 4) {
                applyFullSetBonus(player, suitId);
            } else if (suitCount >= 2) {
                applyPartialBonus(player, suitId);
            }
        }
    }

    private static void applyFullSetBonus(Player player, String suitId) {
        MobEffect effect = getSuitEffect(suitId);
        if (effect != null) {
            MobEffectInstance existing = player.getEffect(effect);
            if (existing == null || existing.getDuration() <= 200) {
                player.addEffect(new MobEffectInstance(effect, 250, 0, false, true, true));
            }
        }
    }

    private static void applyPartialBonus(Player player, String suitId) {
        // Subset bonuses for 2-3 pieces can be defined here
    }

    private static MobEffect getSuitEffect(String suitId) {
        return switch (suitId) {
            case "tough" -> ModEffects.TOUGH.get();
            case "light_leg" -> ModEffects.LIGHT_LEG.get();
            case "heavy_armor" -> ModEffects.HEAVY_ARMOR.get();
            case "rescue" -> ModEffects.RESCUE.get();
            default -> null;
        };
    }

    private static java.util.Set<String> getUniqueSuitIds(Player player) {
        java.util.Set<String> suitIds = new java.util.HashSet<>();
        for (ArmorItem.Type slotType : ArmorItem.Type.values()) {
            ItemStack stack = player.getItemBySlot(slotType.getSlot());
            if (stack.getItem() instanceof LrArmorItem armorItem) {
                suitIds.add(armorItem.getSuitId());
            }
        }
        return suitIds;
    }

    public static void triggerRescueEffect(Player player) {
        MobEffect rescueEffect = ModEffects.RESCUE.get();
        if (rescueEffect != null) {
            player.removeEffect(ModEffects.RESCUE_COOLDOWN.get());
            player.addEffect(new MobEffectInstance(rescueEffect, 200));
            player.addEffect(new MobEffectInstance(ModEffects.RESCUE_COOLDOWN.get(), 600));
        }
    }
}
