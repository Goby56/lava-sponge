package com.goby56.lavasponge.item;

import com.goby56.lavasponge.LavaSponge;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static com.goby56.lavasponge.LavaSponge.MOD_ID;

public class ModItems {

//    public static final Item CODE_NAME = registerItem("id_name",
//            new Item(new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(LavaSponge.MOD_ID, name), item);
    }
    public static void registerModItems() {
        LavaSponge.LOGGER.info("regestering Mod Items for " + MOD_ID);
    }
}
