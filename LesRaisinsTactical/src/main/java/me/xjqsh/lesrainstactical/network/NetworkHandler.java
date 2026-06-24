package me.xjqsh.lesrainstactical.network;

import me.xjqsh.lesrainstactical LesRaisinsTactical;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Vec3;
import net.neoforged.network.api.SimpleChannel;
import net.neoforged.network.api.NetworkManager;
import net.neoforged.network.api.PacketDistributor;
import net.neoforged.network.configuration.ConfigurationDirection;
import net.neoforged.network.configuration.SimpleChannelConfiguration;
import net.neoforged.network.handling.IDirectionalPayload;
import net.neoforged.network.registration.PayloadRegistrar;
import net.neoforged.network.registration.Adapters;

public class NetworkHandler {
    public static final SimpleChannel CHANNEL = NetworkManager.create(
            ResourceLocation.fromNamespaceAndPath(LesRaisinsTactical.MOD_ID, NetworkConstants.NETWORK_NAME),
            () -> NetworkConstants.NETWORK_VERSION,
            it -> it.equals(NetworkConstants.NETWORK_VERSION),
            it -> it.equals(NetworkConstants.NETWORK_VERSION)
    );

    public static void init() {
        PayloadRegistrar registrar = CHANNEL.registrar(NetworkConstants.NETWORK_VERSION);
        
        // Server to Client messages
        registrar.configuration(
                NetworkConstants.PACK_SYNC_ID,
                NetworkConstants.PACK_SYNC_ID,
                ConfigurationDirection.SERVER_TO_CLIENT,
                buf -> {
                    // SPackSyncMessage decoder
                },
                (buf, context) -> {
                    context.enqueueWork(() -> {
                        // SPackSyncMessage handler
                    });
                },
                Adapters.SERVERBOUND
        );
        
        registrar.configuration(
                NetworkConstants.CUSTOM_COOLDOWN_ID,
                NetworkConstants.CUSTOM_COOLDOWN_ID,
                ConfigurationDirection.SERVER_TO_CLIENT,
                buf -> {
                    // SCustomCoolDownMessage decoder
                },
                (buf, context) -> {
                    context.enqueueWork(() -> {
                        // SCustomCoolDownMessage handler
                    });
                },
                Adapters.SERVERBOUND
        );
        
        registrar.configuration(
                NetworkConstants.CUSTOM_SOUND_ID,
                NetworkConstants.CUSTOM_SOUND_ID,
                ConfigurationDirection.SERVER_TO_CLIENT,
                buf -> {
                    // SCustomSound decoder
                },
                (buf, context) -> {
                    context.enqueueWork(() -> {
                        // SCustomSound handler
                    });
                },
                Adapters.SERVERBOUND
        );
        
        registrar.configuration(
                NetworkConstants.SHIELD_SHAKE_ID,
                NetworkConstants.SHIELD_SHAKE_ID,
                ConfigurationDirection.SERVER_TO_CLIENT,
                buf -> {
                    // SShieldShake decoder
                },
                (buf, context) -> {
                    context.enqueueWork(() -> {
                        // SShieldShake handler
                    });
                },
                Adapters.SERVERBOUND
        );
        
        registrar.configuration(
                NetworkConstants.SHIELD_DISABLE_ID,
                NetworkConstants.SHIELD_DISABLE_ID,
                ConfigurationDirection.SERVER_TO_CLIENT,
                buf -> {
                    // SShieldDisable decoder
                },
                (buf, context) -> {
                    context.enqueueWork(() -> {
                        // SShieldDisable handler
                    });
                },
                Adapters.SERVERBOUND
        );
        
        registrar.configuration(
                NetworkConstants.SHAKE_SCREEN_ID,
                NetworkConstants.SHAKE_SCREEN_ID,
                ConfigurationDirection.SERVER_TO_CLIENT,
                buf -> {
                    // SShakeScreenMessage decoder
                },
                (buf, context) -> {
                    context.enqueueWork(() -> {
                        // SShakeScreenMessage handler
                    });
                },
                Adapters.SERVERBOUND
        );
        
        registrar.configuration(
                NetworkConstants.SPLASH_PARTICLE_ID,
                NetworkConstants.SPLASH_PARTICLE_ID,
                ConfigurationDirection.SERVER_TO_CLIENT,
                buf -> {
                    // SSplashParticle decoder
                },
                (buf, context) -> {
                    context.enqueueWork(() -> {
                        // SSplashParticle handler
                    });
                },
                Adapters.SERVERBOUND
        );
        
        registrar.configuration(
                NetworkConstants.MELEE_ANIMATION_SYNC_ID,
                NetworkConstants.MELEE_ANIMATION_SYNC_ID,
                ConfigurationDirection.SERVER_TO_CLIENT,
                buf -> {
                    // SMeleeAnimationSync decoder
                },
                (buf, context) -> {
                    context.enqueueWork(() -> {
                        // SMeleeAnimationSync handler
                    });
                },
                Adapters.SERVERBOUND
        );
        
        registrar.configuration(
                NetworkConstants.RESET_MELEE_SYNC_ID,
                NetworkConstants.RESET_MELEE_SYNC_ID,
                ConfigurationDirection.SERVER_TO_CLIENT,
                buf -> {
                    // SResetMeleeSyncMessage decoder
                },
                (buf, context) -> {
                    context.enqueueWork(() -> {
                        // SResetMeleeSyncMessage handler
                    });
                },
                Adapters.SERVERBOUND
        );
        
        // Client to Server messages
        registrar.configuration(
                NetworkConstants.MELEE_ATTACK_REQUEST_ID,
                NetworkConstants.MELEE_ATTACK_REQUEST_ID,
                ConfigurationDirection.CLIENT_TO_SERVER,
                buf -> {
                    // CMeleeAttackRequest decoder
                },
                (buf, context) -> {
                    context.enqueueWork(() -> {
                        // CMeleeAttackRequest handler
                    });
                },
                Adapters.CLIENTBOUND
        );
        
        registrar.configuration(
                NetworkConstants.PREPARE_MELEE_ATTACK_ID,
                NetworkConstants.PREPARE_MELEE_ATTACK_ID,
                ConfigurationDirection.CLIENT_TO_SERVER,
                buf -> {
                    // CPrepareMeleeAttack decoder
                },
                (buf, context) -> {
                    context.enqueueWork(() -> {
                        // CPrepareMeleeAttack handler
                    });
                },
                Adapters.CLIENTBOUND
        );
        
        registrar.configuration(
                NetworkConstants.CANCEL_CONSUMABLE_USE_ID,
                NetworkConstants.CANCEL_CONSUMABLE_USE_ID,
                ConfigurationDirection.CLIENT_TO_SERVER,
                buf -> {
                    // CCancelToggleConsumableUse decoder
                },
                (buf, context) -> {
                    context.enqueueWork(() -> {
                        // CCancelToggleConsumableUse handler
                    });
                },
                Adapters.CLIENTBOUND
        );
    }

    public static void sendToServer(Object message) {
        CHANNEL.sendToServer(message);
    }

    public static void sendToClientPlayer(Object message, Player player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), message);
    }

    public static void sendToTrackingEntityAndSelf(Entity centerEntity, Object message) {
        CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> centerEntity), message);
    }

    public static void sendToAllPlayers(Object message) {
        CHANNEL.send(PacketDistributor.ALL.noArg(), message);
    }

    public static void sendToTrackingEntity(Object message, Entity centerEntity) {
        CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> centerEntity), message);
    }

    public static void sendToDimension(Object message, Entity centerEntity) {
        CHANNEL.send(PacketDistributor.DIMENSION.with(() -> centerEntity.level().dimension()), message);
    }

    public static void sendToNearbyPlayers(Object message, Level level, Vec3 position, double radius) {
        CHANNEL.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(
                position.x, position.y, position.z, radius, level.dimension()
        )), message);
    }
}
