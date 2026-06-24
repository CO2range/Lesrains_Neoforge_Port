package me.xjqsh.lesrainstactical.compat.aeronautics;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

import java.util.IdentityHashMap;
import java.util.Map;

public class BlockMassTable {
    private static final Map<Block, Float> massMap = new IdentityHashMap<>();
    private static final Map<Block, Float> toughnessMap = new IdentityHashMap<>();

    static {
        register(Blocks.AIR, 0f, 0f);
        register(Blocks.STONE, 20f, 15f);
        register(Blocks.COBBLESTONE, 18f, 12f);
        register(Blocks.MOSSY_COBBLESTONE, 17f, 11f);
        register(Blocks.STONE_BRICKS, 22f, 18f);
        register(Blocks.CRACKED_STONE_BRICKS, 20f, 14f);
        register(Blocks.MOSSY_STONE_BRICKS, 20f, 15f);
        register(Blocks.SMOOTH_STONE, 21f, 16f);
        register(Blocks.POLISHED_ANDESITE, 20f, 15f);
        register(Blocks.POLISHED_DIORITE, 19f, 14f);
        register(Blocks.POLISHED_GRANITE, 20f, 15f);
        register(Blocks.DEEPSLATE, 25f, 20f);
        register(Blocks.DEEPSLATE_BRICKS, 28f, 24f);
        register(Blocks.COBBLED_DEEPSLATE, 22f, 18f);
        register(Blocks.TUFF, 20f, 16f);

        register(Blocks.DIRT, 8f, 3f);
        register(Blocks.GRASS_BLOCK, 9f, 4f);
        register(Blocks.MYCELIUM, 8f, 3f);
        register(Blocks.PODZOL, 9f, 4f);
        register(Blocks.GRAVEL, 10f, 4f);
        register(Blocks.SAND, 6f, 2f);
        register(Blocks.RED_SAND, 6f, 2f);
        register(Blocks.SUSPICIOUS_SAND, 5f, 2f);
        register(Blocks.CLAY, 10f, 4f);

        register(Blocks.GRASS, 0.5f, 0.5f);
        register(Blocks.TALL_GRASS, 0.3f, 0.3f);
        register(Blocks.FERN, 0.5f, 0.5f);
        register(Blocks.LARGE_FERN, 0.3f, 0.3f);
        register(Blocks.DANDELION, 0.2f, 0.2f);
        register(Blocks.POPPY, 0.2f, 0.2f);

        register(Blocks.OAK_LOG, 12f, 8f);
        register(Blocks.SPRUCE_LOG, 14f, 9f);
        register(Blocks.BIRCH_LOG, 10f, 7f);
        register(Blocks.JUNGLE_LOG, 13f, 8f);
        register(Blocks.ACACIA_LOG, 12f, 8f);
        register(Blocks.DARK_OAK_LOG, 15f, 10f);
        register(Blocks.CHERRY_LOG, 9f, 6f);
        register(Blocks.MANGROVE_LOG, 14f, 9f);
        register(Blocks.BAMBOO, 4f, 3f);
        register(Blocks.BAMBOO_BLOCK, 10f, 7f);

        register(Blocks.OAK_PLANKS, 8f, 6f);
        register(Blocks.SPRUCE_PLANKS, 9f, 7f);
        register(Blocks.BIRCH_PLANKS, 7f, 5f);
        register(Blocks.JUNGLE_PLANKS, 9f, 6f);
        register(Blocks.ACACIA_PLANKS, 8f, 6f);
        register(Blocks.DARK_OAK_PLANKS, 10f, 7f);
        register(Blocks.CHERRY_PLANKS, 6f, 5f);
        register(Blocks.MANGROVE_PLANKS, 10f, 7f);
        register(Blocks.BAMBOO_PLANKS, 7f, 5f);

        register(Blocks.OAK_LEAVES, 1f, 1f);
        register(Blocks.SPRUCE_LEAVES, 1.5f, 1.5f);
        register(Blocks.BIRCH_LEAVES, 1f, 1f);

        register(Blocks.GLASS, 5f, 3f);
        register(Blocks.GLASS_PANE, 2f, 2f);
        register(Blocks.TINTED_GLASS, 5.5f, 3.5f);

        register(Blocks.IRON_BLOCK, 40f, 35f);
        register(Blocks.IRON_BARS, 10f, 8f);
        register(Blocks.CHAIN, 12f, 10f);
        register(Blocks.RAW_IRON_BLOCK, 38f, 30f);
        register(Blocks.IRON_DOOR, 15f, 12f);
        register(Blocks.IRON_TRAPDOOR, 8f, 7f);

        register(Blocks.GOLD_BLOCK, 50f, 30f);
        register(Blocks.RAW_GOLD_BLOCK, 48f, 28f);
        register(Blocks.COPPER_BLOCK, 45f, 32f);
        register(Blocks.RAW_COPPER_BLOCK, 42f, 28f);
        register(Blocks.CUT_COPPER, 45f, 35f);
        register(Blocks.WEATHERED_COPPER, 44f, 33f);

        register(Blocks.DIAMOND_BLOCK, 80f, 100f);
        register(Blocks.EMERALD_BLOCK, 70f, 90f);
        register(Blocks.REDSTONE_BLOCK, 35f, 25f);
        register(Blocks.LAPIS_BLOCK, 45f, 30f);
        register(Blocks.AMETHYST_BLOCK, 25f, 20f);

        register(Blocks.OBSIDIAN, 100f, 200f);
        register(Blocks.CRYING_OBSIDIAN, 100f, 200f);
        register(Blocks.BEDROCK, 500f, 1000f);
        register(Blocks.END_STONE, 30f, 25f);
        register(Blocks.END_STONE_BRICKS, 35f, 30f);
        register(Blocks.PURPUR_BLOCK, 12f, 8f);

        register(Blocks.NETHERRACK, 6f, 4f);
        register(Blocks.NETHER_BRICKS, 25f, 22f);
        register(Blocks.RED_NETHER_BRICKS, 28f, 25f);
        register(Blocks.BASALT, 22f, 18f);
        register(Blocks.POLISHED_BASALT, 23f, 19f);
        register(Blocks.BLACKSTONE, 24f, 20f);
        register(Blocks.POLISHED_BLACKSTONE_BRICKS, 26f, 23f);
        register(Blocks.ANCIENT_DEBRIS, 80f, 120f);
        register(Blocks.NETHERITE_BLOCK, 120f, 250f);

        register(Blocks.BRICKS, 24f, 20f);
        register(Blocks.TERRACOTTA, 18f, 14f);
        register(Blocks.WHITE_TERRACOTTA, 17f, 13f);
        register(Blocks.RED_TERRACOTTA, 18f, 14f);
        register(Blocks.ORANGE_TERRACOTTA, 18f, 14f);
        register(Blocks.YELLOW_TERRACOTTA, 17f, 13f);
        register(Blocks.GREEN_TERRACOTTA, 18f, 14f);
        register(Blocks.CYAN_TERRACOTTA, 17f, 13f);
        register(Blocks.LIGHT_BLUE_TERRACOTTA, 17f, 13f);
        register(Blocks.BLUE_TERRACOTTA, 18f, 14f);
        register(Blocks.PURPLE_TERRACOTTA, 18f, 14f);
        register(Blocks.MAGENTA_TERRACOTTA, 17f, 13f);
        register(Blocks.PINK_TERRACOTTA, 17f, 13f);
        register(Blocks.GRAY_TERRACOTTA, 18f, 14f);
        register(Blocks.LIGHT_GRAY_TERRACOTTA, 17f, 13f);
        register(Blocks.BLACK_TERRACOTTA, 18f, 14f);

        register(Blocks.WHITE_CONCRETE, 22f, 18f);
        register(Blocks.RED_CONCRETE, 23f, 19f);
        register(Blocks.ORANGE_CONCRETE, 22f, 18f);
        register(Blocks.YELLOW_CONCRETE, 21f, 17f);
        register(Blocks.GREEN_CONCRETE, 22f, 18f);
        register(Blocks.CYAN_CONCRETE, 22f, 18f);
        register(Blocks.LIGHT_BLUE_CONCRETE, 21f, 17f);
        register(Blocks.BLUE_CONCRETE, 22f, 18f);
        register(Blocks.PURPLE_CONCRETE, 22f, 18f);
        register(Blocks.MAGENTA_CONCRETE, 21f, 17f);
        register(Blocks.PINK_CONCRETE, 21f, 17f);
        register(Blocks.GRAY_CONCRETE, 22f, 18f);
        register(Blocks.LIGHT_GRAY_CONCRETE, 21f, 17f);
        register(Blocks.BLACK_CONCRETE, 23f, 19f);

        register(Blocks.WHITE_WOOL, 3f, 2f);
        register(Blocks.RED_WOOL, 3f, 2f);
        register(Blocks.ORANGE_WOOL, 3f, 2f);
        register(Blocks.YELLOW_WOOL, 3f, 2f);
        register(Blocks.GREEN_WOOL, 3f, 2f);
        register(Blocks.CYAN_WOOL, 3f, 2f);
        register(Blocks.LIGHT_BLUE_WOOL, 3f, 2f);
        register(Blocks.BLUE_WOOL, 3f, 2f);
        register(Blocks.PURPLE_WOOL, 3f, 2f);
        register(Blocks.MAGENTA_WOOL, 3f, 2f);
        register(Blocks.PINK_WOOL, 3f, 2f);
        register(Blocks.GRAY_WOOL, 3f, 2f);
        register(Blocks.LIGHT_GRAY_WOOL, 3f, 2f);
        register(Blocks.BLACK_WOOL, 3f, 2f);
        register(Blocks.BROWN_WOOL, 3f, 2f);

        register(Blocks.PISTON, 20f, 15f);
        register(Blocks.STICKY_PISTON, 20f, 15f);
        register(Blocks.DISPENSER, 25f, 18f);
        register(Blocks.DROPPER, 20f, 15f);
        register(Blocks.HOPPER, 22f, 16f);

        register(Blocks.FURNACE, 20f, 15f);
        register(Blocks.BLAST_FURNACE, 30f, 25f);
        register(Blocks.SMOKER, 18f, 12f);
        register(Blocks.BREWING_STAND, 15f, 10f);

        register(Blocks.CHEST, 12f, 8f);
        register(Blocks.TRAPPED_CHEST, 13f, 8f);
        register(Blocks.BARREL, 10f, 7f);

        register(Blocks.HONEY_BLOCK, 10f, 8f);
        register(Blocks.SLIME_BLOCK, 8f, 5f);

        register(Blocks.ANVIL, 50f, 60f);
        register(Blocks.CHIPPED_ANVIL, 45f, 50f);
        register(Blocks.DAMAGED_ANVIL, 40f, 40f);

        register(Blocks.ENCHANTING_TABLE, 15f, 12f);
        register(Blocks.BOOKSHELF, 10f, 6f);
        register(Blocks.LECTERN, 12f, 8f);

        register(Blocks.CRAFTING_TABLE, 10f, 7f);
        register(Blocks.CARTOGRAPHY_TABLE, 10f, 6f);
        register(Blocks.FLETCHING_TABLE, 9f, 6f);
        register(Blocks.SMITHING_TABLE, 15f, 12f);
        register(Blocks.STONECUTTER, 20f, 16f);
        register(Blocks.GRINDSTONE, 18f, 13f);
        register(Blocks.LOOM, 12f, 8f);

        register(Blocks.LADDER, 3f, 3f);
        register(Blocks.SCAFFOLDING, 4f, 3f);

        register(Blocks.COAL_BLOCK, 30f, 20f);
        register(Blocks.CHARCOAL_BLOCK, 20f, 12f);

        register(Blocks.PRISMARINE, 20f, 16f);
        register(Blocks.PRISMARINE_BRICKS, 22f, 18f);
        register(Blocks.DARK_PRISMARINE, 24f, 20f);
        register(Blocks.SEA_LANTERN, 12f, 8f);

        register(Blocks.PUMPKIN, 8f, 5f);
        register(Blocks.CARVED_PUMPKIN, 7f, 4f);
        register(Blocks.JACK_O_LANTERN, 7f, 4f);
        register(Blocks.MELON, 8f, 4f);
        register(Blocks.HAY_BLOCK, 6f, 3f);

        register(Blocks.NETHER_WART_BLOCK, 5f, 3f);
        register(Blocks.WARPED_WART_BLOCK, 5f, 3f);
        register(Blocks.SHROOMLIGHT, 5f, 4f);
        register(Blocks.GLOWSTONE, 8f, 5f);
        register(Blocks.SEA_PICKLE, 1f, 1f);

        register(Blocks.CAKE, 3f, 2f);
        register(Blocks.SPONGE, 5f, 3f);
        register(Blocks.WET_SPONGE, 12f, 5f);
    }

    private static void register(Block block, float mass, float toughness) {
        massMap.put(block, mass);
        toughnessMap.put(block, toughness);
    }

    public static float getMass(BlockState state) {
        Float mass = massMap.get(state.getBlock());
        if (mass != null) return mass;

        MapColor color = state.getMapColor(null, null);
        if (color == null) return 15f;

        return switch (color.id) {
            case 0, 26, 37 -> 0f;
            case 1, 2, 3 -> 10f;
            case 4, 5, 6 -> 20f;
            case 7, 8, 9 -> 8f;
            case 10, 11, 12 -> 25f;
            case 13, 14, 15 -> 40f;
            case 22, 23, 24 -> 12f;
            case 38, 39, 40 -> 30f;
            case 44, 45, 46 -> 35f;
            default -> 15f;
        };
    }

    public static float getToughness(BlockState state) {
        Float toughness = toughnessMap.get(state.getBlock());
        if (toughness != null) return toughness;

        return getMass(state) * 0.7f;
    }

    public static boolean isHeavyBlock(BlockState state) {
        return getMass(state) >= 50f;
    }

    public static boolean isLightBlock(BlockState state) {
        return getMass(state) <= 10f;
    }
}
