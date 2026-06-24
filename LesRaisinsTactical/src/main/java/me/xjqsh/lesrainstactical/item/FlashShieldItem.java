package me.xjqsh.lesrainstactical.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import me.xjqsh.lesrainstactical.api.collision.ConeFilter;
import me.xjqsh.lesrainstactical.api.item.IMeleeWeapon;
import me.xjqsh.lesrainstactical.api.melee.MeleeAction;
import me.xjqsh.lesrainstactical.capability.CombatPropertiesProvider;
import me.xjqsh.lesrainstactical.capability.CustomItemCoolDownsProvider;
import me.xjqsh.lesrainstactical.config.ServerConfig;
import me.xjqsh.lesrainstactical.init.ModEffects;
import me.xjqsh.lesrainstactical.item.throwable.flash.StunThrowableData;
import me.xjqsh.lesrainstactical.util.SightTraceUtil;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Consumer;

public class FlashShieldItem extends Item implements IMeleeWeapon {
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public FlashShieldItem() {
        super(new Item.Properties().stacksTo(1).durability(350));
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier("Shield modifier", -0.25, AttributeModifier.Operation.MULTIPLY_BASE));
        defaultModifiers = builder.build();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if (slot == EquipmentSlot.MAINHAND) {
            return defaultModifiers;
        }
        return ImmutableMultimap.of();
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return ServerConfig.FLASH_SHIELD_MAX_DURABILITY.get();
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public HumanoidModel.ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
                if (hand == InteractionHand.OFF_HAND) {
                    return HumanoidModel.ArmPose.EMPTY;
                }
                return HumanoidModel.ArmPose.CROSSBOW_HOLD;
            }

            @Override
            public int getFontDisplayHeight(int displayHeight) {
                return displayHeight;
            }
        });
    }

    @Override
    public int getDrawTime(ItemStack stack) {
        return 10;
    }

    @Override
    public void attack(Player attacker, ItemStack stack, MeleeAction action, List<Entity> targets, int cnt) {
        float base = (float) attacker.getAttributeValue(Attributes.ATTACK_DAMAGE);
        for (Entity livingentity : targets) {
            boolean flag = !(livingentity instanceof ArmorStand armorStand) || !armorStand.isMarker();
            boolean inRange = livingentity.distanceToSqr(attacker) <= 2.5 * 2.5;

            if (livingentity != attacker && flag && inRange) {
                this.performAttack(attacker, livingentity, stack, base, 1.2f);
            }
        }
    }

    @Override
    public List<Entity> collectTargets(Player attacker, ItemStack stack, MeleeAction action, Vec3 origin, Vec3 direction) {
        ConeFilter filter = new ConeFilter(2.5f, 105);
        return filter.filterTargets(attacker, origin, direction);
    }

    @Override
    public boolean canSprintingAttack() {
        return false;
    }

    @Override
    public int getAttackDelay(Player attacker, ItemStack stack, MeleeAction action, int cnt) {
        return 5;
    }

    @Override
    public boolean canAttack(Player attacker, ItemStack stack, MeleeAction action) {
        boolean isDisabled = attacker.getCapability(CustomItemCoolDownsProvider.CAPABILITY)
                .map(cap -> cap.isOnCooldown(new ResourceLocation("shield_disabled")))
                .orElse(false);
        return action == MeleeAction.LEFT && !isDisabled;
    }

    @Override
    public boolean shouldBlockUse() {
        return false;
    }

    @Override
    public int getMaxUsingTick(ItemStack stack) {
        return 10;
    }

    @ParametersAreNonnullByDefault
    @Override
    public int getUseDuration(ItemStack pStack) {
        return 10;
    }

    @ParametersAreNonnullByDefault
    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player player, InteractionHand pUsedHand) {
        if (pUsedHand == InteractionHand.OFF_HAND) {
            return InteractionResultHolder.fail(player.getItemInHand(pUsedHand));
        }
        boolean coolDown = player.getCapability(CombatPropertiesProvider.CAPABILITY)
                .map(cap -> cap.getCoolDownTick() > 0)
                .orElse(false);
        boolean isDisabled = player.getCapability(CustomItemCoolDownsProvider.CAPABILITY)
                .map(cap -> cap.isOnCooldown(new ResourceLocation("shield_disabled")))
                .orElse(false);
        if (coolDown || isDisabled) {
            return InteractionResultHolder.fail(player.getItemInHand(pUsedHand));
        }
        ItemStack stack = player.getItemInHand(pUsedHand);
        player.startUsingItem(pUsedHand);
        return InteractionResultHolder.sidedSuccess(stack, pLevel.isClientSide());
    }

    @ParametersAreNonnullByDefault
    @Override
    public void onUseTick(Level pLevel, LivingEntity pLivingEntity, ItemStack pStack, int pRemainingUseDuration) {
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
        if (entity.getTicksUsingItem() >= this.getMaxUsingTick(stack)) {
            if (!world.isClientSide()) {
                if (entity instanceof Player player) {
                    player.getCooldowns().addCooldown(stack.getItem(), ServerConfig.FLASH_SHIELD_COOLDOWN.get());
                    player.addEffect(new MobEffectInstance(ModEffects.BLIND.get(), 45, 0, false, false));
                    player.addEffect(new MobEffectInstance(ModEffects.DEAFENED.get(), 60, 0, false, false));
                }

                AABB aabb = entity.getBoundingBox().inflate(12);
                for (Entity target : world.getEntities(entity, aabb, EntitySelector.NO_SPECTATORS)) {
                    if (target instanceof LivingEntity living) {
                        calculateAndApplyEffect(entity, living, data);
                    }
                }
            }
        }
        return stack;
    }

    public static StunThrowableData.StunData data = new StunThrowableData.StunData();

    public static void calculateAndApplyEffect(Entity starter, LivingEntity target, StunThrowableData.StunData data) {
        Vec3 p = starter.position().add(0.0, 1.0, 0.0);
        Vec3 eyes = target.getEyePosition(1.0F);
        Vec3 d1 = p.subtract(eyes);
        Vec3 d2 = eyes.subtract(p);

        double distanceMax = data.getRadius();
        double distance = d1.length();

        if (distance > distanceMax) {
            return;
        }

        double a1 = Math.toDegrees(Math.acos(target.getViewVector(1.0F).dot(d1.normalize())));
        double a2 = Math.toDegrees(Math.acos(starter.getViewVector(1.0F).dot(d2.normalize())));
        double angleMax = data.getBlind().getMaxAngle();

        if (a1 > 0 && a1 < angleMax && a2 > 0 && a2 <= 90.0) {
            if (SightTraceUtil.rayTraceOpaqueBlocks(starter, target.level(), eyes, p, false, false, false) == null) {
                int durationBlinded = data.calcBlindDuration(distance, a1);
                if (durationBlinded > 0) {
                    target.addEffect(new MobEffectInstance(ModEffects.BLIND.get(), durationBlinded, 0, false, false, true));
                }
            }
        }

        int durationDeafened = data.calcDeafenedDuration(distance);
        if (durationDeafened > 0) {
            target.addEffect(new MobEffectInstance(ModEffects.DEAFENED.get(), durationDeafened, 0, false, false, true));
        }
    }

    @Override
    public boolean useOnRelease(@NotNull ItemStack pStack) {
        return false;
    }

    @Override
    public boolean isSame(ItemStack stack1, ItemStack stack2) {
        return ItemStack.isSameItem(stack1, stack2);
    }
}