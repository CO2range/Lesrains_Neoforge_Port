package me.xjqsh.lesrainstactical.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class SuitEffect extends MobEffect {
    private final String suitType;

    public SuitEffect(MobEffectCategory category, int color, String suitType) {
        super(category, color);
        this.suitType = suitType;
    }

    public String getSuitType() {
        return suitType;
    }
}
