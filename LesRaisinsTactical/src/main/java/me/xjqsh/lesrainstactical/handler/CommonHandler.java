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
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = "lesrainstactical", bus = EventBusSubscriber.Bus.GAME)
public class CommonHandler {

    @SubscribeEvent
    public static void onLivingTick(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof Player player) {
            handlePlayerSuitEffects(player);
        }
    }

    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack from = event.getFrom();
            ItemStack to = event.getTo();
            
            // Handle effect removal when armor is removed
            if (from.getItem() instanceof LrArmorItem fromArmor) {
                String suitId = fromArmor.getSuitId();
                int remainingCount = LrArmorItem.getSuitCount(player, suitId) - 1;
                
                if (remainingCount <= 0) {
                    MobEffect effect = fromArmor.getSuitEffect() != null ? 
                            fromArmor.getSuitEffect().get() : null;
                    if (effect != null) {
                        player.removeEffect(effect);
                    }
                }
            }
            
            // Handle effect application when armor is equipped
            if (to.getItem() instanceof LrArmorItem toArmor) {
                toArmor.applyEffect(player);
            }
        }
    }

    private static void handlePlayerSuitEffects(Player player) {
        for (ArmorItem.Type slotType : ArmorItem.Type.values()) {
            ItemStack stack = player.getItemBySlot(slotType.getSlot());
            if (stack.getItem() instanceof LrArmorItem armorItem) {
                // Apply suit effect if 4 pieces are equipped
                if (LrArmorItem.getSuitCount(player, armorItem.getSuitId()) >= 4) {
                    armorItem.applyEffect(player);
                }
            }
        }
    }

    public static void applySuitEffect(Player player, MobEffect effect) {
        if (effect == null) return;
        
        // Check if player already has the effect with high enough duration
        MobEffectInstance existing = player.getEffect(effect);
        if (existing == null || existing.getDuration() < 200) {
            player.addEffect(new MobEffectInstance(effect, 250));
        }
    }
}
