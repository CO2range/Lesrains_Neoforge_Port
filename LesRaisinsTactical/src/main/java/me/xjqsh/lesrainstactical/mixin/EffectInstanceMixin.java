package me.xjqsh.lesrainstactical.mixin;

import me.xjqsh.lesrainstactical.init.ModEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEffectInstance.class)
public abstract class EffectInstanceMixin {

    @Inject(method = "shouldApplyEffectTickThisTick", at = @At("HEAD"), cancellable = true)
    private void onShouldApplyEffectTick(int duration, int amplifier, CallbackInfoReturnable<Boolean> cir) {
        // Handle rescue_cooldown effect - only tick once then remove
        Object self = this;
        if (self instanceof MobEffectInstance instance) {
            MobEffect effect = instance.getEffect();
            if (effect != null && effect.equals(ModEffects.RESCUE_COOLDOWN.get())) {
                if (duration <= 1) {
                    cir.setReturnValue(true);
                } else {
                    cir.setReturnValue(false);
                }
            }
        }
    }

    @Inject(method = "update", at = @At("HEAD"))
    private void onUpdate(MobEffectInstance other, CallbackInfoReturnable<MobEffectInstance> cir) {
        // Handle rescue effect application
        Object self = this;
        if (self instanceof MobEffectInstance instance) {
            MobEffect effect = instance.getEffect();
            if (effect != null && effect.equals(ModEffects.RESCUE.get())) {
                // Rescue effect logic can be handled here if needed
            }
        }
    }
}
