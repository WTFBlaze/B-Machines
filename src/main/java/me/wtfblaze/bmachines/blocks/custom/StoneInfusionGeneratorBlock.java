package me.wtfblaze.bmachines.blocks.custom;

import me.wtfblaze.bmachines.blocks.entity.BBlockEntities;
import me.wtfblaze.bmachines.blocks.entity.StoneInfusionGeneratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class StoneInfusionGeneratorBlock extends BaseEntityBlock
{
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public StoneInfusionGeneratorBlock(Properties properties)
    {
        super(properties);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    // Block Entity Methods


    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving)
    {
        if (pState.getBlock() != pNewState.getBlock())
        {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof StoneInfusionGeneratorBlockEntity sigEntity)
            {
                sigEntity.drops();
            }
        }
        super.onRemove(pState, pLevel, pPos, pState, pIsMoving);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        if (!pLevel.isClientSide())
        {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof StoneInfusionGeneratorBlockEntity sigBlockEntity)
            {
                NetworkHooks.openScreen((ServerPlayer)player, sigBlockEntity, pPos);
            }
            else
            {
                throw new IllegalStateException("Our Stone Infusion Generator container provider is missing!");
            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new StoneInfusionGeneratorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
        return createTickerHelper(type, BBlockEntities.STONE_INFUSION_GENERATOR.get(), StoneInfusionGeneratorBlockEntity::tick);
    }
}
