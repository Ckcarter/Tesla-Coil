package com.che.teslacoil;

import com.che.teslacoil.registry.ModBlockEntities;
import com.che.teslacoil.registry.ModBlocks;
import com.che.teslacoil.registry.ModItems;
import com.che.teslacoil.registry.ModMenus;
import com.che.teslacoil.sound.ModSounds.ModSounds;
import com.che.teslacoil.network.ModNetworking;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Mod(TeslaCoilMod.MODID)
public class TeslaCoilMod {
    public static final String MODID = "teslacoil";

    private static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<CreativeModeTab> TESLA_TAB = TABS.register("tesla_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("creativetab.teslacoil"))
                    .icon(() -> new ItemStack(ModItems.TESLA_COIL_ITEM.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.TESLA_COIL_ITEM.get());
                        output.accept(ModItems.CREATIVE_POWER_BLOCK_ITEM.get());
                    })
                    .build());

    public TeslaCoilMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.BLOCKS.register(bus);
        ModItems.ITEMS.register(bus);
        ModBlockEntities.BLOCK_ENTITIES.register(bus);
        ModMenus.MENUS.register(bus);
        ModSounds.register(bus);
        TABS.register(bus);
        ModNetworking.register();
    }
}
