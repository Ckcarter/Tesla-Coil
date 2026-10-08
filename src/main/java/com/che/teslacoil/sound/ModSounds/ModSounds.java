package com.che.teslacoil.sound.ModSounds;

import com.che.teslacoil.TeslaCoilMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModSounds {
    private static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, TeslaCoilMod.MODID);

    public static final RegistryObject<SoundEvent> CHARGE_HUM = SOUND_EVENTS.register(
            "charge_hum", () -> SoundEvent.createVariableRangeEvent(
                    new ResourceLocation(TeslaCoilMod.MODID, "charge_hum")));
    public static final RegistryObject<SoundEvent> PLASMA_SNAP = SOUND_EVENTS.register(
            "plasma_snap", () -> SoundEvent.createVariableRangeEvent(
                    new ResourceLocation(TeslaCoilMod.MODID, "plasma_snap")));

    public static void register(IEventBus bus) {
        SOUND_EVENTS.register(bus);
    }

    private ModSounds() {}
}
