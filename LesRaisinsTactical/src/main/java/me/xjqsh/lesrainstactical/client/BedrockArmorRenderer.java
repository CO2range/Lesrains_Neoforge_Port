package me.xjqsh.lesrainstactical.client;

import me.xjqsh.lesrainstactical LesRaisinsTactical;
import me.xjqsh.lesrainstactical.item.LrArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class BedrockArmorRenderer extends GeoArmorRenderer<LrArmorItem> {
    private static final ResourceLocation ANIMATION_LOCATION = new ResourceLocation(LesRaisinsTactical.MOD_ID, "animations/item/armor/default.animation.json");

    public BedrockArmorRenderer(String suit) {
        super(new DefaultedItemGeoModel<>(new ResourceLocation(LesRaisinsTactical.MOD_ID, "armor/" + suit)) {
            @Override
            public ResourceLocation getAnimationResource(LrArmorItem animatable) {
                return ANIMATION_LOCATION;
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(LrArmorItem animatable) {
        return super.getTextureLocation(animatable);
    }
}
