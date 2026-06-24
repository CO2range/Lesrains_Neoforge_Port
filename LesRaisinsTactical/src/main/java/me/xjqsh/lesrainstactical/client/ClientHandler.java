package me.xjqsh.lesrainstactical.client;

import me.xjqsh.lesrainstactical.init.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = "lesrainstactical", bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ClientHandler {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        handleClientEffects(mc);
    }

    private static void handleClientEffects(Minecraft mc) {
        // Handle blinded effect
        MobEffectInstance blindEffect = mc.player.getEffect(ModEffects.BLIND.get());
        if (blindEffect != null) {
            handleBlindEffect(mc, blindEffect);
        }

        // Handle deafened effect
        MobEffectInstance deafenEffect = mc.player.getEffect(ModEffects.DEAFENED.get());
        if (deafenEffect != null) {
            handleDeafenEffect(mc, deafenEffect);
        }
    }

    private static void handleBlindEffect(Minecraft mc, MobEffectInstance effect) {
        // Client-side blind effect handling (screen darkening, etc.)
    }

    private static void handleDeafenEffect(Minecraft mc, MobEffectInstance effect) {
        // Client-side deafen effect handling (sound muffling, etc.)
    }

    public static void triggerRescueEffect() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.addEffect(new MobEffectInstance(ModEffects.RESCUE.get(), 200));
        }
    }
}
