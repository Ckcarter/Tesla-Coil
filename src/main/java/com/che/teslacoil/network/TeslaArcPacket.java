package com.che.teslacoil.network;

import com.che.teslacoil.client.TeslaArcManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record TeslaArcPacket(BlockPos coilPos, int targetEntityId) {
    public static void encode(TeslaArcPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.coilPos);
        buf.writeVarInt(msg.targetEntityId);
    }

    public static TeslaArcPacket decode(FriendlyByteBuf buf) {
        return new TeslaArcPacket(buf.readBlockPos(), buf.readVarInt());
    }

    public static void handle(TeslaArcPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                        () -> () -> TeslaArcManager.addArc(msg.coilPos, msg.targetEntityId)));
        ctx.get().setPacketHandled(true);
    }
}
