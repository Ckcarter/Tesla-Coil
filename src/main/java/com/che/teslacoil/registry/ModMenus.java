package com.che.teslacoil.registry;

import com.che.teslacoil.TeslaCoilMod;
import com.che.teslacoil.menu.TeslaCoilMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, TeslaCoilMod.MODID);

    public static final RegistryObject<MenuType<TeslaCoilMenu>> TESLA_COIL =
            MENUS.register("tesla_coil", () -> IForgeMenuType.create(TeslaCoilMenu::new));

    private ModMenus() {}
}
