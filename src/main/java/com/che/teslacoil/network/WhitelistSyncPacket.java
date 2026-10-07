package com.che.teslacoil.network;

import com.che.teslacoil.client.TeslaCoilScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public record WhitelistSyncPacket(List<String> names) {
    public static void encode(WhitelistSyncPacket m, FriendlyByteBuf b) {
        b.writeVarInt(m.names.size());
        for (String s : m.names) b.writeUtf(s, 64);
    }
    public static WhitelistSyncPacket decode(FriendlyByteBuf b) {
        int count=b.readVarInt(); List<String> names=new ArrayList<>();
        for(int i=0;i<count;i++) names.add(b.readUtf(64));
        return new WhitelistSyncPacket(names);
    }
    public static void handle(WhitelistSyncPacket m, Supplier<NetworkEvent.Context> sup) {
        NetworkEvent.Context ctx=sup.get();
        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            if (Minecraft.getInstance().screen instanceof TeslaCoilScreen screen) screen.setNames(m.names);
        }));
        ctx.setPacketHandled(true);
    }
}
