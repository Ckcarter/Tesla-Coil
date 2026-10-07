package com.che.teslacoil.block;

import com.che.teslacoil.blockentity.TeslaCoilBlockEntity;
import com.che.teslacoil.menu.TeslaCoilMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

public class TeslaCoilExtensionBlock extends Block {
    private final int baseOffset;

    public TeslaCoilExtensionBlock(Properties properties, int baseOffset) {
        super(properties);
        this.baseOffset = baseOffset;
    }

    private BlockPos base(BlockPos pos) {
        return pos.below(baseOffset);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (!player.isShiftKeyDown()) return InteractionResult.PASS;
        BlockPos basePos = base(pos);
        if (!(level.getBlockEntity(basePos) instanceof TeslaCoilBlockEntity coil)) return InteractionResult.PASS;

        if (!coil.isOwner(player.getUUID())) {
            if (!level.isClientSide)
                player.sendSystemMessage(Component.literal("Only the Tesla Coil owner can open its whitelist."));
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openScreen(serverPlayer, new MenuProvider() {
                @Override public Component getDisplayName() { return Component.literal("Tesla Coil Whitelist"); }
                @Override public AbstractContainerMenu createMenu(int id, Inventory inv, Player p) {
                    return new TeslaCoilMenu(id, inv, basePos);
                }
            }, basePos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        BlockPos basePos = base(pos);
        if (!level.isClientSide) {
            // Breaking an upper section breaks the base; the base cleans up the other upper section.
            level.destroyBlock(basePos, !player.isCreative(), player);
        }
        super.playerWillDestroy(level, pos, state, player);
    }
}
