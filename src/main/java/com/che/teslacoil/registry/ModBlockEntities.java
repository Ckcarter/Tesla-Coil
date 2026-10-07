package com.che.teslacoil.registry;

import com.che.teslacoil.TeslaCoilMod;
import com.che.teslacoil.blockentity.TeslaCoilBlockEntity;
import com.che.teslacoil.blockentity.CreativePowerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, TeslaCoilMod.MODID);

    public static final RegistryObject<BlockEntityType<TeslaCoilBlockEntity>> TESLA_COIL =
            BLOCK_ENTITIES.register("tesla_coil",
                    () -> BlockEntityType.Builder.of(TeslaCoilBlockEntity::new,
                            ModBlocks.TESLA_COIL.get()).build(null));

    public static final RegistryObject<BlockEntityType<CreativePowerBlockEntity>> CREATIVE_POWER_BLOCK =
            BLOCK_ENTITIES.register("creative_power_block",
                    () -> BlockEntityType.Builder.of(CreativePowerBlockEntity::new,
                            ModBlocks.CREATIVE_POWER_BLOCK.get()).build(null));

    private ModBlockEntities() {}
}
