package com.che.teslacoil.blockentity;

import com.che.teslacoil.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CreativePowerBlockEntity extends BlockEntity {
    private static final int OUTPUT_PER_TICK = 100_000;

    private final IEnergyStorage infiniteEnergy = new IEnergyStorage() {
        public int receiveEnergy(int maxReceive, boolean simulate) { return 0; }
        public int extractEnergy(int maxExtract, boolean simulate) { return maxExtract; }
        public int getEnergyStored() { return Integer.MAX_VALUE; }
        public int getMaxEnergyStored() { return Integer.MAX_VALUE; }
        public boolean canExtract() { return true; }
        public boolean canReceive() { return false; }
    };

    private LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(() -> infiniteEnergy);

    public CreativePowerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CREATIVE_POWER_BLOCK.get(), pos, state);
    }

    public static void serverTick(CreativePowerBlockEntity self) {
        if (self.level == null) return;
        for (Direction direction : Direction.values()) {
            BlockEntity neighbor = self.level.getBlockEntity(self.worldPosition.relative(direction));
            if (neighbor == null) continue;
            neighbor.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).ifPresent(storage -> {
                if (storage.canReceive()) storage.receiveEnergy(OUTPUT_PER_TICK, false);
            });
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) return energyCap.cast();
        return super.getCapability(cap, side);
    }

    @Override public void invalidateCaps() {
        super.invalidateCaps();
        energyCap.invalidate();
    }

    @Override public void reviveCaps() {
        super.reviveCaps();
        energyCap = LazyOptional.of(() -> infiniteEnergy);
    }
}
