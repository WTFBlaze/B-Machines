package me.wtfblaze.bmachines.blocks.entity;

import me.wtfblaze.bmachines.BMachines;
import me.wtfblaze.bmachines.blocks.BBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BBlockEntities
{
    public static DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, BMachines.MODID);

    public static final RegistryObject<BlockEntityType<StoneInfusionGeneratorBlockEntity>> STONE_INFUSION_GENERATOR =
            BLOCK_ENTITIES.register("stone_infusion_generator", () ->
                    BlockEntityType.Builder.of(StoneInfusionGeneratorBlockEntity::new,
                            BBlocks.STONE_INFUSION_GENERATOR.get()).build(null));

    public static void register(IEventBus eventBus)
    {
        BLOCK_ENTITIES.register(eventBus);
    }
}
