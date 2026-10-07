package com.che.teslacoil.command;

import com.che.teslacoil.TeslaCoilMod;
import com.che.teslacoil.blockentity.TeslaCoilBlockEntity;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = TeslaCoilMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class TeslaCoilCommands {
    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("teslacoil")
                .then(Commands.literal("whitelist")
                        .then(Commands.literal("add")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(c -> add(c.getSource(), EntityArgument.getPlayer(c, "player")))))
                        .then(Commands.literal("remove")
                                .then(Commands.argument("player", StringArgumentType.word())
                                        .executes(c -> remove(c.getSource(), StringArgumentType.getString(c, "player")))))
                        .then(Commands.literal("list")
                                .executes(c -> list(c.getSource())))));
    }

    private static int add(CommandSourceStack source, ServerPlayer target) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer owner = source.getPlayerOrException();
        TeslaCoilBlockEntity coil = getOwnedLookedAtCoil(owner);
        if (coil == null) return 0;

        if (coil.isOwner(target.getUUID())) {
            source.sendFailure(Component.literal("The Tesla Coil owner is already protected."));
            return 0;
        }

        if (!coil.addWhitelistedPlayer(target.getUUID())) {
            source.sendFailure(Component.literal(target.getGameProfile().getName() + " is already whitelisted."));
            return 0;
        }

        source.sendSuccess(() -> Component.literal(target.getGameProfile().getName() + " added to this Tesla Coil whitelist.")
                .withStyle(ChatFormatting.GREEN), false);
        return 1;
    }

    private static int remove(CommandSourceStack source, String name) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer owner = source.getPlayerOrException();
        TeslaCoilBlockEntity coil = getOwnedLookedAtCoil(owner);
        if (coil == null) return 0;

        for (UUID uuid : coil.getWhitelistedPlayers()) {
            String playerName = owner.server.getProfileCache().get(uuid).map(p -> p.getName()).orElse("");
            if (playerName.equalsIgnoreCase(name)) {
                coil.removeWhitelistedPlayer(uuid);
                source.sendSuccess(() -> Component.literal(playerName + " removed from this Tesla Coil whitelist.")
                        .withStyle(ChatFormatting.YELLOW), false);
                return 1;
            }
        }

        source.sendFailure(Component.literal(name + " is not on this Tesla Coil whitelist."));
        return 0;
    }

    private static int list(CommandSourceStack source) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer owner = source.getPlayerOrException();
        TeslaCoilBlockEntity coil = getOwnedLookedAtCoil(owner);
        if (coil == null) return 0;

        if (coil.getWhitelistedPlayers().isEmpty()) {
            source.sendSuccess(() -> Component.literal("This Tesla Coil whitelist is empty."), false);
            return 1;
        }

        source.sendSuccess(() -> Component.literal("Tesla Coil whitelist:").withStyle(ChatFormatting.AQUA), false);
        for (UUID uuid : coil.getWhitelistedPlayers()) {
            String name = owner.server.getProfileCache().get(uuid).map(p -> p.getName()).orElse(uuid.toString());
            source.sendSuccess(() -> Component.literal(" - " + name), false);
        }
        return 1;
    }

    private static TeslaCoilBlockEntity getOwnedLookedAtCoil(ServerPlayer player) {
        Vec3 start = player.getEyePosition();
        Vec3 end = start.add(player.getLookAngle().scale(8.0));
        BlockHitResult hit = player.level().clip(new ClipContext(
                start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));

        if (hit.getType() != HitResult.Type.BLOCK ||
                !(player.level().getBlockEntity(hit.getBlockPos()) instanceof TeslaCoilBlockEntity coil)) {
            player.sendSystemMessage(Component.literal("Look directly at a Tesla Coil within 8 blocks.")
                    .withStyle(ChatFormatting.RED));
            return null;
        }

        if (!coil.isOwner(player.getUUID())) {
            player.sendSystemMessage(Component.literal("Only this Tesla Coil's owner can change its whitelist.")
                    .withStyle(ChatFormatting.RED));
            return null;
        }

        return coil;
    }

    private TeslaCoilCommands() {}
}
