package me.xjqsh.lesrainstactical.client;

import me.xjqsh.lesrainstactical.init.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = "lesrainstactical", bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ClientSetupHandler {

    @SubscribeEvent
    public static void onClientSetup(net.neoforged.fml.event.lifecycle.FMLClientSetupEvent event) {
        // Register client-side rendering and GUI handlers
    }

    public static boolean isBlinded() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            MobEffectInstance effect = mc.player.getEffect(ModEffects.BLIND.get());
            return effect != null;
        }
        return false;
    }

    public static boolean isDeafened() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            MobEffectInstance effect = mc.player.getEffect(ModEffects.DEAFENED.get());
            return effect != null;
        }
        return false;
    }
}
