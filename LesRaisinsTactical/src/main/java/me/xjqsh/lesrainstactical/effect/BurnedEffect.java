package me.xjqsh.lesrainstactical.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class BurnedEffect extends MobEffect {
    public BurnedEffect(int color) {
        super(MobEffectCategory.HARMFUL, color);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public int applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.fireImmune()) {
            entity.setRemainingFireTicks(40);
        }
        return duration;
    }
}
