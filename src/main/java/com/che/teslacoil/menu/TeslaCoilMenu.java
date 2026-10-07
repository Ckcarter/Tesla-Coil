package com.che.teslacoil.menu;

import com.che.teslacoil.blockentity.TeslaCoilBlockEntity;
import com.che.teslacoil.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class TeslaCoilMenu extends AbstractContainerMenu {
    private final BlockPos coilPos;

    public TeslaCoilMenu(int id, Inventory inventory, FriendlyByteBuf buf) {
        this(id, inventory, buf.readBlockPos());
    }

    public TeslaCoilMenu(int id, Inventory inventory, BlockPos pos) {
        super(ModMenus.TESLA_COIL.get(), id);
        this.coilPos = pos;
    }

    public BlockPos getCoilPos() {
        return coilPos;
    }

    @Override
    public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        if (!(player.level().getBlockEntity(coilPos) instanceof TeslaCoilBlockEntity coil)) {
            return false;
        }
        return coil.isOwner(player.getUUID())
                && player.distanceToSqr(
                        coilPos.getX() + 0.5,
                        coilPos.getY() + 0.5,
                        coilPos.getZ() + 0.5) <= 64.0;
    }
}
