package com.goby56.lavasponge.block;

import com.goby56.lavasponge.LavaSponge;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModBlocks {

    public static final Block DRY_LAVA_SPONGE_BLOCK = registerBlock("lava_sponge",
            new LavaSpongeBlock(FabricBlockSettings.of(Material.SPONGE).strength(1f, 1f).requiresTool().sounds(BlockSoundGroup.ANCIENT_DEBRIS)), ItemGroup.BUILDING_BLOCKS, true);

    public static final Block WET_LAVA_SPONGE_BLOCK = registerBlock("wet_lava_sponge",
            new WetLavaSpongeBlock(FabricBlockSettings.of(Material.SPONGE).strength(1.2f, 1.2f).requiresTool().sounds(BlockSoundGroup.NETHER_GOLD_ORE)), ItemGroup.BUILDING_BLOCKS, true);

    private static Block registerBlock(String name, Block block, ItemGroup group, boolean fireproof) {
        registerBlockItem(name, block, group, fireproof);
        return Registry.register(Registry.BLOCK, new Identifier(LavaSponge.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block, ItemGroup group, boolean fireproof) {
        if (fireproof) {
            return Registry.register(Registry.ITEM, new Identifier(LavaSponge.MOD_ID, name),
                    new BlockItem(block, new FabricItemSettings().group(group).fireproof()));
        } else {
            return Registry.register(Registry.ITEM, new Identifier(LavaSponge.MOD_ID, name),
                    new BlockItem(block, new FabricItemSettings().group(group)));
        }
    }

    public static void registerModBlocks() {
        LavaSponge.LOGGER.info("Registering Mod Blocks for " + LavaSponge.MOD_ID);
    }
}
