package me.wtfblaze.bmachines.world.feature;

import com.google.common.base.Suppliers;
import me.wtfblaze.bmachines.BMachines;
import me.wtfblaze.bmachines.blocks.BBlocks;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.Supplier;

public class BConfiguredFeatures
{
    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES = DeferredRegister.create(Registry.CONFIGURED_FEATURE_REGISTRY, BMachines.MODID);

    public static final Supplier<List<OreConfiguration.TargetBlockState>> OVERWORLD_DYNANIUM_ORES = Suppliers.memoize(() -> List.of(
            OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, BBlocks.DYNANIUM_ORE.get().defaultBlockState()),
            OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, BBlocks.DEEPSLATE_DYNANIUM_ORE.get().defaultBlockState())));

    public static final RegistryObject<ConfiguredFeature<?, ?>> DYNANIUM_ORE = CONFIGURED_FEATURES.register("dynanium_ore",
            () -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(OVERWORLD_DYNANIUM_ORES.get(), 7)));

    public static void register(IEventBus eventBus)
    {
        CONFIGURED_FEATURES.register(eventBus);
    }
}
