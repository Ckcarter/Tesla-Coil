package com.che.teslacoil.blockentity;

import com.che.teslacoil.registry.ModBlockEntities;
import com.che.teslacoil.network.ModNetworking;
import com.che.teslacoil.network.TeslaArcPacket;
import net.minecraftforge.network.PacketDistributor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.HashSet;
import java.util.Set;

public class TeslaCoilBlockEntity extends BlockEntity {
    public static final int RANGE = 15;
    public static final int CAPACITY = 100_000;
    public static final int ENERGY_PER_STRIKE = 2_500;
    public static final int ATTACK_INTERVAL = 10;

    private UUID ownerUUID;
    private final Set<UUID> whitelistedPlayers = new HashSet<>();
    private int tickCounter;

    private final EnergyStorage energy = new EnergyStorage(CAPACITY, 5_000, ENERGY_PER_STRIKE) {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int received = super.receiveEnergy(maxReceive, simulate);
            if (received > 0 && !simulate) setChanged();
            return received;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int extracted = super.extractEnergy(maxExtract, simulate);
            if (extracted > 0 && !simulate) setChanged();
            return extracted;
        }
    };

    private LazyOptional<EnergyStorage> energyCapability = LazyOptional.of(() -> energy);

    public TeslaCoilBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TESLA_COIL.get(), pos, state);
    }

    public void setOwner(UUID uuid) {
        this.ownerUUID = uuid;
        setChanged();
    }

    @Nullable
    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public boolean isOwner(UUID uuid) {
        return ownerUUID != null && ownerUUID.equals(uuid);
    }

    public boolean addWhitelistedPlayer(UUID uuid) {
        boolean changed = whitelistedPlayers.add(uuid);
        if (changed) setChanged();
        return changed;
    }

    public boolean removeWhitelistedPlayer(UUID uuid) {
        boolean changed = whitelistedPlayers.remove(uuid);
        if (changed) setChanged();
        return changed;
    }

    public Set<UUID> getWhitelistedPlayers() {
        return Set.copyOf(whitelistedPlayers);
    }

    public static void serverTick(ServerLevel level, BlockPos pos,
                                  BlockState state, TeslaCoilBlockEntity coil) {
        if (++coil.tickCounter < ATTACK_INTERVAL) return;
        coil.tickCounter = 0;

        if (coil.energy.getEnergyStored() < ENERGY_PER_STRIKE) return;

        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 0.8;
        double cz = pos.getZ() + 0.5;

        AABB area = new AABB(pos).inflate(RANGE);
        List<LivingEntity> targets = level.getEntitiesOfClass(
                LivingEntity.class,
                area,
                target -> target.isAlive()
                        && !target.isSpectator()
                        && (coil.ownerUUID == null || !target.getUUID().equals(coil.ownerUUID))
                        && !(target instanceof ServerPlayer && coil.whitelistedPlayers.contains(target.getUUID()))
                        && target.distanceToSqr(cx, cy, cz) <= RANGE * RANGE
        );

        LivingEntity target = targets.stream()
                .min(Comparator.comparingDouble(e -> e.distanceToSqr(cx, cy, cz)))
                .orElse(null);

        if (target == null) return;

        coil.energy.extractEnergy(ENERGY_PER_STRIKE, false);

        // Tell nearby clients to render a short-lived jagged arc from the top terminal to the victim.
        ModNetworking.CHANNEL.send(
                PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        64.0, level.dimension())),
                new TeslaArcPacket(pos, target.getId()));

        // Extremely high damage: intended to make a powered coil lethal.
        target.hurt(level.damageSources().magic(), 1000.0F);

        level.playSound(null, pos, SoundEvents.LIGHTNING_BOLT_IMPACT,
                SoundSource.BLOCKS, 1.0F, 1.2F);

        // Vanilla electric/lightning-style flash/sound event at the target.
        level.levelEvent(3002, target.blockPosition(), 0);

        coil.setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (ownerUUID != null) tag.putUUID("Owner", ownerUUID);
        net.minecraft.nbt.ListTag whitelist = new net.minecraft.nbt.ListTag();
        for (UUID uuid : whitelistedPlayers) {
            whitelist.add(net.minecraft.nbt.StringTag.valueOf(uuid.toString()));
        }
        tag.put("Whitelist", whitelist);
        tag.putInt("Energy", energy.getEnergyStored());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        ownerUUID = tag.hasUUID("Owner") ? tag.getUUID("Owner") : null;
        whitelistedPlayers.clear();
        net.minecraft.nbt.ListTag whitelist = tag.getList("Whitelist", net.minecraft.nbt.Tag.TAG_STRING);
        for (int i = 0; i < whitelist.size(); i++) {
            try {
                whitelistedPlayers.add(UUID.fromString(whitelist.getString(i)));
            } catch (IllegalArgumentException ignored) {}
        }

        int savedEnergy = tag.getInt("Energy");
        if (savedEnergy > 0) {
            energy.receiveEnergy(Math.min(savedEnergy, CAPACITY), false);
        }
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energyCapability.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        energyCapability = LazyOptional.of(() -> energy);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(
            @NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return energyCapability.cast();
        }
        return super.getCapability(cap, side);
    }
}
