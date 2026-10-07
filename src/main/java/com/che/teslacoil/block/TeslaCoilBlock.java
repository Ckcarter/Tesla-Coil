package com.che.teslacoil.block;

import com.che.teslacoil.blockentity.TeslaCoilBlockEntity;
import com.che.teslacoil.registry.ModBlockEntities;
import com.che.teslacoil.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import com.che.teslacoil.menu.TeslaCoilMenu;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class TeslaCoilBlock extends BaseEntityBlock {
    public TeslaCoilBlock(Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TeslaCoilBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state,
                            @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide && placer != null &&
                level.getBlockEntity(pos) instanceof TeslaCoilBlockEntity coil) {
            coil.setOwner(placer.getUUID());
            if (level.getBlockState(pos.above()).canBeReplaced() &&
                    level.getBlockState(pos.above(2)).canBeReplaced()) {
                level.setBlock(pos.above(), ModBlocks.TESLA_COIL_MIDDLE.get().defaultBlockState(), 3);
                level.setBlock(pos.above(2), ModBlocks.TESLA_COIL_TOP.get().defaultBlockState(), 3);
            }
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        if (!level.getBlockState(pos.above()).canBeReplaced() ||
                !level.getBlockState(pos.above(2)).canBeReplaced()) {
            return null;
        }
        return defaultBlockState();
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            if (level.getBlockState(pos.above()).is(ModBlocks.TESLA_COIL_MIDDLE.get()))
                level.removeBlock(pos.above(), false);
            if (level.getBlockState(pos.above(2)).is(ModBlocks.TESLA_COIL_TOP.get()))
                level.removeBlock(pos.above(2), false);
        }
        super.playerWillDestroy(level, pos, state, player);
    }


    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (!player.isShiftKeyDown()) return InteractionResult.PASS;
        if (!(level.getBlockEntity(pos) instanceof TeslaCoilBlockEntity coil)) return InteractionResult.PASS;

        if (!coil.isOwner(player.getUUID())) {
            if (!level.isClientSide) player.sendSystemMessage(Component.literal("Only the Tesla Coil owner can open its whitelist."));
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openScreen(serverPlayer, new MenuProvider() {
                @Override public Component getDisplayName() { return Component.literal("Tesla Coil Whitelist"); }
                @Override public AbstractContainerMenu createMenu(int id, Inventory inv, Player p) {
                    return new TeslaCoilMenu(id, inv, pos);
                }
            }, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) return null;
        return createTickerHelper(type, ModBlockEntities.TESLA_COIL.get(),
                (lvl, pos, blockState, coil) ->
                        TeslaCoilBlockEntity.serverTick((ServerLevel) lvl, pos, blockState, coil));
    }
}
