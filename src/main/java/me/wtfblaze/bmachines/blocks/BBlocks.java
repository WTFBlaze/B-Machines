package me.wtfblaze.bmachines.blocks;

import me.wtfblaze.bmachines.BMachines;
import me.wtfblaze.bmachines.blocks.custom.StoneInfusionGeneratorBlock;
import me.wtfblaze.bmachines.items.BItems;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class BBlocks
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, BMachines.MODID);

    public static final RegistryObject<Block> DYNANIUM_BLOCK = registerBlock("dynanium_block", () ->
            new Block(BlockBehaviour.Properties.of(Material.STONE)
                    .strength(6f).requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> DYNANIUM_ORE = registerBlock("dynanium_ore", () ->
            new DropExperienceBlock(BlockBehaviour.Properties.of(Material.STONE)
                    .strength(6f).requiresCorrectToolForDrops(),
                    UniformInt.of(3, 7)));

    public static final RegistryObject<Block> DEEPSLATE_DYNANIUM_ORE = registerBlock("deepslate_dynanium_ore", () ->
            new DropExperienceBlock(BlockBehaviour.Properties.of(Material.STONE)
                    .strength(6f).requiresCorrectToolForDrops(),
                    UniformInt.of(3, 7)));

    public static final RegistryObject<Block> STONE_INFUSION_GENERATOR = registerBlock("stone_infusion_generator",
            () -> new StoneInfusionGeneratorBlock(BlockBehaviour.Properties.of(Material.METAL)
                    .strength(6f).requiresCorrectToolForDrops().noOcclusion()));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block)
    {
        RegistryObject<T> returnObj = BLOCKS.register(name, block);
        registerBlockItem(name, returnObj);
        return returnObj;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block)
    {
        return BItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(BMachines.TAB)));
    }

    public static void register(IEventBus eventBus)
    {
        BLOCKS.register(eventBus);
        BMachines.LOGGER.info("Registered BMachines Blocks!");
    }
}
