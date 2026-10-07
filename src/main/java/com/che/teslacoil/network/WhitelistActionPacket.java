package com.che.teslacoil.network;

import com.che.teslacoil.blockentity.TeslaCoilBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public record WhitelistActionPacket(BlockPos pos, int action, String name) {
    public static void encode(WhitelistActionPacket m, FriendlyByteBuf b) {
        b.writeBlockPos(m.pos); b.writeVarInt(m.action); b.writeUtf(m.name, 64);
    }
    public static WhitelistActionPacket decode(FriendlyByteBuf b) {
        return new WhitelistActionPacket(b.readBlockPos(), b.readVarInt(), b.readUtf(64));
    }
    public static void handle(WhitelistActionPacket m, Supplier<NetworkEvent.Context> sup) {
        NetworkEvent.Context ctx = sup.get();
        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();
            if (sender == null || sender.distanceToSqr(m.pos.getX()+0.5,m.pos.getY()+0.5,m.pos.getZ()+0.5) > 64) return;
            if (!(sender.level().getBlockEntity(m.pos) instanceof TeslaCoilBlockEntity coil) || !coil.isOwner(sender.getUUID())) return;

            if (m.action == 0) {
                sender.server.getProfileCache().get(m.name).ifPresent(profile -> {
                    if (!profile.getId().equals(sender.getUUID())) coil.addWhitelistedPlayer(profile.getId());
                });
            } else if (m.action == 1) {
                for (UUID id : coil.getWhitelistedPlayers()) {
                    String n = sender.server.getProfileCache().get(id).map(p -> p.getName()).orElse("");
                    if (n.equalsIgnoreCase(m.name)) { coil.removeWhitelistedPlayer(id); break; }
                }
            }
            sendList(sender, coil);
        });
        ctx.setPacketHandled(true);
    }
    private static void sendList(ServerPlayer player, TeslaCoilBlockEntity coil) {
        List<String> names = new ArrayList<>();
        for (UUID id : coil.getWhitelistedPlayers())
            names.add(player.server.getProfileCache().get(id).map(p -> p.getName()).orElse(id.toString()));
        names.sort(String.CASE_INSENSITIVE_ORDER);
        ModNetworking.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new WhitelistSyncPacket(names));
    }
}
