package me.xjqsh.lesrainstactical.compat.sable;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SableProjectileManager {
    private static final Map<UUID, PhysicsProjectile> projectiles = new ConcurrentHashMap<>();

    public static void registerProjectile(PhysicsProjectile projectile) {
        if (projectile.getSubLevelId() != null) {
            projectiles.put(projectile.getSubLevelId(), projectile);
        }
    }

    public static void unregisterProjectile(PhysicsProjectile projectile) {
        if (projectile.getSubLevelId() != null) {
            projectiles.remove(projectile.getSubLevelId());
        }
    }

    public static PhysicsProjectile getProjectile(UUID subLevelId) {
        return projectiles.get(subLevelId);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.LevelTickEvent event) {
        if (event.level.isClientSide()) return;
        if (event.phase != TickEvent.Phase.END) return;

        for (PhysicsProjectile proj : projectiles.values()) {
            if (proj.isAlive()) {
                proj.tick();
            }
        }
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        projectiles.entrySet().removeIf(entry ->
                entry.getValue().getLevel() == event.getLevel()
        );
    }

    public static int getActiveProjectileCount() {
        return projectiles.size();
    }
}
