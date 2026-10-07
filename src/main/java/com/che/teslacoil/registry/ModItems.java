package com.che.teslacoil.registry;

import com.che.teslacoil.TeslaCoilMod;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, TeslaCoilMod.MODID);

    public static final RegistryObject<Item> TESLA_COIL_ITEM = ITEMS.register("tesla_coil",
            () -> new BlockItem(ModBlocks.TESLA_COIL.get(), new Item.Properties()));

    public static final RegistryObject<Item> CREATIVE_POWER_BLOCK_ITEM = ITEMS.register("creative_power_block",
            () -> new BlockItem(ModBlocks.CREATIVE_POWER_BLOCK.get(), new Item.Properties()));

    private ModItems() {}
}
