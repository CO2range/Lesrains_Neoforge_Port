package me.xjqsh.lesrainstactical.compat.aeronautics;

import me.xjqsh.lesrainstactical.compat.sable.SableCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class HoneyGlueHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger("LesRaisinsTactical:Glue");

    public static boolean removeHoneyGlueAt(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);

        if (state.is(Blocks.HONEY_BLOCK)) {
            if (SableCompat.isSableLoaded() && SableCompat.isInSubLevel(level, pos)) {
                level.destroyBlock(pos, false);
                return true;
            }
        }

        return checkAndRemoveGlueConnections(level, pos);
    }

    private static boolean checkAndRemoveGlueConnections(Level level, BlockPos pos) {
        boolean removedAny = false;

        for (Direction direction : Direction.values()) {
            BlockPos adjacentPos = pos.relative(direction);
            BlockState adjacentState = level.getBlockState(adjacentPos);

            if (adjacentState.is(Blocks.HONEY_BLOCK)) {
                if (SableCompat.isSableLoaded() && SableCompat.isInSubLevel(level, adjacentPos)) {
                    if (level.random.nextFloat() < 0.7f) {
                        if (level instanceof ServerLevel serverLevel) {
                            serverLevel.destroyBlock(adjacentPos, false);
                            spawnGlueBreakParticles(serverLevel, adjacentPos);
                        }
                        removedAny = true;
                    }
                }
            }
        }

        return removedAny;
    }

    public static int removeHoneyGlueInRadius(Level level, BlockPos center, float radius) {
        if (!SableCompat.isSableLoaded()) return 0;

        Set<BlockPos> glueBlocks = new HashSet<>();
        int r = (int) Math.ceil(radius);

        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    BlockPos pos = center.offset(x, y, z);
                    double dist = Math.sqrt(x * x + y * y + z * z);
                    if (dist > radius) continue;

                    BlockState state = level.getBlockState(pos);
                    if (state.is(Blocks.HONEY_BLOCK) && SableCompat.isInSubLevel(level, pos)) {
                        glueBlocks.add(pos);
                    }
                }
            }
        }

        int removed = 0;
        for (BlockPos pos : glueBlocks) {
            float distFactor = 1f - (float) Math.sqrt(pos.distSqr(center)) / radius;
            float chance = 0.8f * distFactor + 0.2f;

            if (level.random.nextFloat() < chance) {
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.destroyBlock(pos, false);
                    spawnGlueBreakParticles(serverLevel, pos);
                }
                removed++;
            }
        }

        return removed;
    }

    private static void spawnGlueBreakParticles(ServerLevel level, BlockPos pos) {
        level.sendParticles(net.minecraft.core.particles.ParticleTypes.FALLING_HONEY,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                10, 0.3, 0.3, 0.3, 0.05);
    }

    public static boolean isHoneyBlock(BlockState state) {
        return state.is(Blocks.HONEY_BLOCK);
    }

    public static boolean hasGlueConnection(Level level, BlockPos pos, Direction direction) {
        BlockPos adjacent = pos.relative(direction);
        BlockState state = level.getBlockState(adjacent);
        return isHoneyBlock(state);
    }

    public static int countGlueConnections(Level level, BlockPos pos) {
        int count = 0;
        for (Direction dir : Direction.values()) {
            if (hasGlueConnection(level, pos, dir)) count++;
        }
        return count;
    }
}
