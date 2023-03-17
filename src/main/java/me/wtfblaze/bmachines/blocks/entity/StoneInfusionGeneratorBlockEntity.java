package me.wtfblaze.bmachines.blocks.entity;

import me.wtfblaze.bmachines.blocks.custom.StoneInfusionGeneratorBlock;
import me.wtfblaze.bmachines.networking.BNetworkMessages;
import me.wtfblaze.bmachines.networking.packet.EnergySyncS2CPacket;
import me.wtfblaze.bmachines.networking.packet.FluidSyncS2CPacket;
import me.wtfblaze.bmachines.screen.StoneInfusionGeneratorMenu;
import me.wtfblaze.bmachines.util.BEnergyStorage;
import me.wtfblaze.bmachines.util.BWrapperHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class StoneInfusionGeneratorBlockEntity extends BlockEntity implements MenuProvider
{
    private final ItemStackHandler itemHandler = new ItemStackHandler(2)
    {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                case 0 -> stack.getItem() == Items.STONE;
                case 1 -> stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent();
                default -> super.isItemValid(slot, stack);
            };
        }
    };

    private final BEnergyStorage energyHandler = new BEnergyStorage(10000, 256)
    {
        @Override
        public void onEnergyChanged()
        {
            setChanged();
            BNetworkMessages.sendToClients(new EnergySyncS2CPacket(this.energy, getBlockPos()));
        }

        @Override
        public boolean canExtract() {
            return true;
        }
    };

    private final FluidTank fluidHandler = new FluidTank(10000)
    {
        @Override
        protected void onContentsChanged()
        {
            setChanged();
            assert level != null;
            if (!level.isClientSide())
                BNetworkMessages.sendToClients(new FluidSyncS2CPacket(this.fluid, worldPosition));
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == Fluids.LAVA;
        }
    };

    public void setFluid(FluidStack stack) {
        fluidHandler.setFluid(stack);
    }

    public FluidStack getFluidStack() {
        return fluidHandler.getFluid();
    }

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();
    private LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();

    private final Map<Direction, LazyOptional<BWrapperHandler>> directionWrappedHandlerMap =
            Map.of(
                    Direction.DOWN, LazyOptional.of(() -> new BWrapperHandler(itemHandler, (i) -> i == 2, (i, s) -> false)),

                    Direction.NORTH, LazyOptional.of(() -> new BWrapperHandler(itemHandler, (index) -> index == 1,
                            (index, stack) -> itemHandler.isItemValid(1, stack))),

                    Direction.SOUTH, LazyOptional.of(() -> new BWrapperHandler(itemHandler, (i) -> i == 2, (i, s) -> false)),

                    Direction.EAST, LazyOptional.of(() -> new BWrapperHandler(itemHandler, (i) -> i == 1,
                            (index, stack) -> itemHandler.isItemValid(1, stack))),

                    Direction.WEST, LazyOptional.of(() -> new BWrapperHandler(itemHandler, (index) -> index == 0 || index == 1,
                            (index, stack) -> itemHandler.isItemValid(0, stack) || itemHandler.isItemValid(1, stack))));

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 78;

    public StoneInfusionGeneratorBlockEntity(BlockPos pos, BlockState state)
    {
        super(BBlockEntities.STONE_INFUSION_GENERATOR.get(), pos, state);
        this.data = new ContainerData()
        {
            @Override
            public int get(int index)
            {
                return switch (index)
                {
                    case 0 -> StoneInfusionGeneratorBlockEntity.this.progress;
                    case 1 -> StoneInfusionGeneratorBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value)
            {
                switch (index)
                {
                    case 0 -> StoneInfusionGeneratorBlockEntity.this.progress = value;
                    case 1 -> StoneInfusionGeneratorBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.literal("Stone Infusion Generator");
    }

    public IEnergyStorage getEnergyStorage(){
        return energyHandler;
    }

    public void setEnergyLevel(int energy) {
        this.energyHandler.setEnergy(energy);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player player) {
        BNetworkMessages.sendToClients(new EnergySyncS2CPacket(this.energyHandler.getEnergyStored(), getBlockPos()));
        BNetworkMessages.sendToClients(new FluidSyncS2CPacket(this.getFluidStack(), worldPosition));
        return new StoneInfusionGeneratorMenu(id, inv, this, this.data);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
    {
        if (cap == ForgeCapabilities.ENERGY){
            return lazyEnergyHandler.cast();
        }

        if (cap == ForgeCapabilities.FLUID_HANDLER){
            return lazyFluidHandler.cast();
        }

        if (cap == ForgeCapabilities.ITEM_HANDLER)
        {
            if (side == null)
                return lazyItemHandler.cast();

            if(directionWrappedHandlerMap.containsKey(side))
            {
                Direction localDir = this.getBlockState().getValue(StoneInfusionGeneratorBlock.FACING);

                if(side == Direction.UP || side == Direction.DOWN) {
                    return directionWrappedHandlerMap.get(side).cast();
                }

                return switch (localDir) {
                    default -> directionWrappedHandlerMap.get(side.getOpposite()).cast();
                    case EAST -> directionWrappedHandlerMap.get(side.getClockWise()).cast();
                    case SOUTH -> directionWrappedHandlerMap.get(side).cast();
                    case WEST -> directionWrappedHandlerMap.get(side.getCounterClockWise()).cast();
                };
            }
        }

        return super.getCapability(cap, side);
    }



    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyEnergyHandler = LazyOptional.of(() -> energyHandler);
        lazyFluidHandler = LazyOptional.of(() -> fluidHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyEnergyHandler.invalidate();
        lazyFluidHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt)
    {
        nbt.put("inventory", itemHandler.serializeNBT());
        nbt.putInt("stone_infusion_generator.progress", progress);
        nbt.putInt("stone_infusion_generator.energy", energyHandler.getEnergyStored());
        nbt = fluidHandler.writeToNBT(nbt);

        super.saveAdditional(nbt);
    }

    @Override
    public void load(@NotNull CompoundTag nbt)
    {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        progress = nbt.getInt("stone_infusion_generator.progress");
        energyHandler.setEnergy(nbt.getInt("stone_infusion_generator.energy"));
        fluidHandler.readFromNBT(nbt);
    }

    public void drops()
    {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        assert this.level != null;
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, StoneInfusionGeneratorBlockEntity entity)
    {
        if (level.isClientSide())
            return;

        if (hasStoneInFirstSlot(entity) && hasEnoughFluid(entity)) {
            entity.progress++;
            setChanged(level, pos, state);

            if (entity.progress >= entity.maxProgress) {
                createEnergy(entity);
                setChanged(level, pos, state);
            }
        }
        else{
            entity.resetProgress();
            setChanged(level, pos, state);
        }

        if (hasLavaInSecondSlot(entity))
        {
            transferItemFluidToFluidTank(entity);
        }
    }

    private static boolean hasStoneInFirstSlot(StoneInfusionGeneratorBlockEntity entity){
        return entity.itemHandler.getStackInSlot(0).getItem() == Items.STONE;
    }

    private static boolean hasLavaInSecondSlot(StoneInfusionGeneratorBlockEntity entity){
        return entity.itemHandler.getStackInSlot(1).getItem() == Items.LAVA_BUCKET;
    }

    private static boolean hasEnoughFluid(StoneInfusionGeneratorBlockEntity entity){
        return entity.fluidHandler.getFluidAmount() > 0;
    }

    private static void createEnergy(StoneInfusionGeneratorBlockEntity entity){
        entity.itemHandler.extractItem(0, 1, false);
        entity.resetProgress();
        entity.energyHandler.receiveEnergy(100, false);
        entity.fluidHandler.drain(100, IFluidHandler.FluidAction.EXECUTE);
    }

    private static void transferItemFluidToFluidTank(StoneInfusionGeneratorBlockEntity entity){
        entity.itemHandler.getStackInSlot(1).getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(handler -> {
            int drainAmount = Math.min(entity.fluidHandler.getSpace(), 1000);
            FluidStack stack = handler.drain(drainAmount, IFluidHandler.FluidAction.SIMULATE);
            if(entity.fluidHandler.isFluidValid(stack)) {
                stack = handler.drain(drainAmount, IFluidHandler.FluidAction.EXECUTE);
                fillTankWithFluid(entity, stack, handler.getContainer());
            }
        });
    }

    private static void fillTankWithFluid(StoneInfusionGeneratorBlockEntity entity, FluidStack stack, ItemStack container){
        entity.fluidHandler.fill(stack, IFluidHandler.FluidAction.EXECUTE);
        entity.itemHandler.extractItem(1, 1, false);
        entity.itemHandler.insertItem(1, container, false);
    }

    private void resetProgress() {
        this.progress = 0;
    }
}
