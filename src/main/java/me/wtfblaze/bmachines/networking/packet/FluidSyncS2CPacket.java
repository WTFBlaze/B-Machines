package me.wtfblaze.bmachines.networking.packet;

import me.wtfblaze.bmachines.blocks.entity.StoneInfusionGeneratorBlockEntity;
import me.wtfblaze.bmachines.screen.StoneInfusionGeneratorMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class FluidSyncS2CPacket
{
    private final FluidStack fluidStack;
    private final BlockPos pos;

    public FluidSyncS2CPacket(FluidStack fluidStack, BlockPos pos){
        this.fluidStack = fluidStack;
        this.pos = pos;
    }

    public FluidSyncS2CPacket(FriendlyByteBuf buf){
        this.fluidStack = buf.readFluidStack();
        this.pos = buf.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeFluidStack(fluidStack);
        buf.writeBlockPos(pos);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof StoneInfusionGeneratorBlockEntity blockEntity){
                blockEntity.setFluid(this.fluidStack);

                if (Minecraft.getInstance().player.containerMenu instanceof StoneInfusionGeneratorMenu menu &&
                        menu.getBlockEntity().getBlockPos().equals(pos)){
                    menu.setFluid(this.fluidStack);
                }
            }
        });
        return true;
    }
}
