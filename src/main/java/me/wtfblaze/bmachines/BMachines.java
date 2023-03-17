package me.wtfblaze.bmachines;

import com.mojang.logging.LogUtils;
import me.wtfblaze.bmachines.blocks.BBlocks;
import me.wtfblaze.bmachines.blocks.entity.BBlockEntities;
import me.wtfblaze.bmachines.items.BItems;
import me.wtfblaze.bmachines.networking.BNetworkMessages;
import me.wtfblaze.bmachines.screen.BScreens;
import me.wtfblaze.bmachines.screen.StoneInfusionGeneratorScreen;
import me.wtfblaze.bmachines.world.feature.BConfiguredFeatures;
import me.wtfblaze.bmachines.world.feature.BPlacedFeatures;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Mod(BMachines.MODID)
public class BMachines
{
    public static final String MODID = "bmachines";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final CreativeModeTab TAB = new CreativeModeTab("bmachines_tab")
    {
        @Override
        public @NotNull ItemStack makeIcon() {
            return new ItemStack(BBlocks.STONE_INFUSION_GENERATOR.get());
        }

        @Override
        public boolean hasSearchBar() {
            return true;
        }
    };

    public BMachines()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        BItems.register(modEventBus);
        BBlocks.register(modEventBus);
        BBlockEntities.register(modEventBus);
        BScreens.register(modEventBus);
        BConfiguredFeatures.register(modEventBus);
        BPlacedFeatures.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event){
        BNetworkMessages.register();
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            MenuScreens.register(BScreens.STONE_INFUSION_GENERATOR_MENU.get(), StoneInfusionGeneratorScreen::new);
        }
    }
}
