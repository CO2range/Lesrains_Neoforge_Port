package me.xjqsh.lesrainstactical.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import me.xjqsh.lesrainstactical.armor.LrArmorMaterial;
import me.xjqsh.lesrainstactical.config.CommonConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LrArmorItem extends ArmorItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final String suitId;
    private final @Nullable Supplier<MobEffect> suitEffect;
    private final int defense;
    private final float toughness;
    private final int maxDurability;
    private final float knockbackResistance;
    private final int enchantmentValue;
    private final SoundEvent equipSound;
    private final @Nullable Ingredient repairIngredient;
    private final Multimap<Attribute, AttributeModifier> attributeModifiers;
    private LrArmorMaterial material;

    public LrArmorItem(String suitId, ArmorItem.Type slot, Properties properties, @Nullable Supplier<MobEffect> suitEffect,
                       int defense, float toughness, int maxDurability, float knockbackResistance,
                       int enchantmentValue, SoundEvent equipSound, @Nullable Ingredient repairIngredient,
                       Multimap<Attribute, AttributeModifier> attributeModifiers) {
        super(LrArmorMaterial.DEFAULT, slot, properties);
        this.suitId = suitId;
        this.suitEffect = suitEffect;
        this.defense = defense;
        this.toughness = toughness;
        this.maxDurability = maxDurability;
        this.knockbackResistance = knockbackResistance;
        this.enchantmentValue = enchantmentValue;
        this.equipSound = equipSound;
        this.repairIngredient = repairIngredient;
        this.attributeModifiers = attributeModifiers;
    }

    public LrArmorItem(String suitId, ArmorItem.Type slot, Properties properties, @Nullable Supplier<MobEffect> suitEffect) {
        this(suitId, slot, properties, suitEffect,
                0, 0.0f, 0, 0.0f, 5, SoundEvents.ARMOR_EQUIP_LEATHER, null,
                ImmutableMultimap.of());
    }

    public String getSuitId() {
        return suitId;
    }

    public void setArmorMaterial(LrArmorMaterial material) {
        this.material = material;
    }

    @Override
    public @NotNull LrArmorMaterial getMaterial() {
        return material != null ? material : LrArmorMaterial.DEFAULT;
    }

    @Override
    public int getDefense() {
        return defense;
    }

    @Override
    public float getToughness() {
        return toughness;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return getMaxDurability();
    }

    public int getMaxDurability() {
        return maxDurability;
    }

    public float getKnockbackResistance() {
        return knockbackResistance;
    }

    @Override
    public int getEnchantmentValue() {
        return enchantmentValue;
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return getMaxDamage(stack) > 0;
    }

    public boolean canBeDepleted() {
        return getMaxDurability() > 0;
    }

    @Override
    public @NotNull SoundEvent getEquipSound() {
        return equipSound;
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack pToRepair, @NotNull ItemStack pRepair) {
        if (repairIngredient != null) {
            return repairIngredient.test(pRepair);
        }
        return false;
    }

    @Nullable
    public Ingredient getIngredient() {
        return repairIngredient;
    }

    @Override
    public @NotNull Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(@NotNull EquipmentSlot pEquipmentSlot) {
        return CommonConfig.enableArmorAttribute.get() && pEquipmentSlot == this.type.getSlot() ?
                attributeModifiers :
                ImmutableMultimap.of();
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private software.bernie.geckolib.renderer.GeoArmorRenderer<?> renderer;

            @Override
            @OnlyIn(Dist.CLIENT)
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack,
                                                                   EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                if (this.renderer == null) {
                    this.renderer = new me.xjqsh.lesrainstactical.client.BedrockArmorRenderer(getSuitId());
                }

                this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, 20, state -> {
            state.setAnimation(DefaultAnimations.IDLE);
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level world, @Nonnull List<Component> list,
                                @Nonnull TooltipFlag tooltipFlag) {
        if (Minecraft.getInstance().player != null) {
            Player player = Minecraft.getInstance().player;
            Component title = Component.translatable("tooltip.lrtactical.suit",
                    Component.translatable("suit.lrtactical." + this.suitId),
                    Component.literal(String.format("(%d/4)", getSuitCount(player, stack)))
            ).withStyle(ChatFormatting.GRAY);
            list.add(title);

            boolean flag = true;
            LrArmorItem item = (LrArmorItem) stack.getItem();
            ItemStack equipItem = player.getItemBySlot(item.getEquipmentSlot());
            if (!stack.equals(equipItem)) {
                flag = false;
            }

            for (ArmorItem.Type slotType : ArmorItem.Type.values()) {
                MutableComponent part = Component.translatable("item.lrtactical." + this.suitId + "_" + slotType.getName());

                if (flag && isPartEquipped(player, slotType)) {
                    part.withStyle(ChatFormatting.GREEN);
                } else {
                    part.withStyle(ChatFormatting.GRAY);
                }

                list.add(part);
            }
        }
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return "lrtactical:item/armor/" + this.suitId + ".png";
    }

    public void applyEffect(Player player) {
        if (suitEffect == null) return;
        MobEffect effect = suitEffect.get();
        if (effect != null) {
            player.addEffect(new MobEffectInstance(effect, 250));
        }
    }

    public boolean isPartEquipped(Player player, ArmorItem.Type slot) {
        ItemStack equipItem = player.getItemBySlot(slot.getSlot());
        if (equipItem.getItem() instanceof LrArmorItem item) {
            return item.getSuitId().equals(this.suitId);
        }
        return false;
    }

    public int getSuitCount(Player player, @Nonnull ItemStack stack) {
        if (!(stack.getItem() instanceof LrArmorItem item)) return 0;

        ItemStack equipItem = player.getItemBySlot(item.getEquipmentSlot());
        if (!stack.equals(equipItem)) {
            return 0;
        }

        return getSuitCount(player);
    }

    public int getSuitCount(Player player) {
        return getSuitCount(player, this.suitId);
    }

    public static int getSuitCount(Player player, String suitId) {
        int cnt = 0;

        for (ArmorItem.Type slotType : ArmorItem.Type.values()) {
            ItemStack stack = player.getItemBySlot(slotType.getSlot());
            if (stack.getItem() instanceof LrArmorItem item) {
                if (item.getSuitId().equals(suitId)) {
                    cnt++;
                }
            }
        }
        return cnt;
    }
}
