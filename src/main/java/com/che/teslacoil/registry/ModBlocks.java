package com.che.teslacoil.registry;

import com.che.teslacoil.TeslaCoilMod;
import com.che.teslacoil.block.TeslaCoilBlock;
import com.che.teslacoil.block.CreativePowerBlock;
import com.che.teslacoil.block.TeslaCoilExtensionBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, TeslaCoilMod.MODID);

    public static final RegistryObject<Block> TESLA_COIL = BLOCKS.register("tesla_coil",
            () -> new TeslaCoilBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(4.0F, 8.0F)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()));

    public static final RegistryObject<Block> TESLA_COIL_MIDDLE = BLOCKS.register("tesla_coil_middle",
            () -> new TeslaCoilExtensionBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL).strength(4.0F, 8.0F)
                    .requiresCorrectToolForDrops().noOcclusion(), 1));

    public static final RegistryObject<Block> TESLA_COIL_TOP = BLOCKS.register("tesla_coil_top",
            () -> new TeslaCoilExtensionBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL).strength(4.0F, 8.0F)
                    .requiresCorrectToolForDrops().noOcclusion(), 2));

    public static final RegistryObject<Block> CREATIVE_POWER_BLOCK = BLOCKS.register("creative_power_block",
            () -> new CreativePowerBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(5.0F, 10.0F)
                    .requiresCorrectToolForDrops()));

    private ModBlocks() {}
}
