package com.che.teslacoil.network;

import com.che.teslacoil.TeslaCoilMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class ModNetworking {
    private static final String PROTOCOL = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(TeslaCoilMod.MODID, "main"),
            () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);

    private static int id = 0;

    public static void register() {
        CHANNEL.registerMessage(id++, TeslaArcPacket.class,
                TeslaArcPacket::encode, TeslaArcPacket::decode, TeslaArcPacket::handle);
        CHANNEL.registerMessage(id++, WhitelistActionPacket.class,
                WhitelistActionPacket::encode, WhitelistActionPacket::decode, WhitelistActionPacket::handle);
        CHANNEL.registerMessage(id++, WhitelistSyncPacket.class,
                WhitelistSyncPacket::encode, WhitelistSyncPacket::decode, WhitelistSyncPacket::handle);
    }

    private ModNetworking() {}
}
