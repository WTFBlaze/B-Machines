package me.wtfblaze.bmachines.screen;

import me.wtfblaze.bmachines.BMachines;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BScreens
{
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, BMachines.MODID);

    public static final RegistryObject<MenuType<StoneInfusionGeneratorMenu>> STONE_INFUSION_GENERATOR_MENU =
            registerMenuType(StoneInfusionGeneratorMenu::new, "stone_infusion_generator_menu");

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(IContainerFactory<T> factory, String name)
    {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus eventBus)
    {
        MENUS.register(eventBus);
    }
}
