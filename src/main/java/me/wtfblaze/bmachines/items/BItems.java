package me.wtfblaze.bmachines.items;

import me.wtfblaze.bmachines.BMachines;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BMachines.MODID);

    public static final RegistryObject<Item> RAW_DYNANIUM = ITEMS.register("raw_dynanium", () ->
            new Item(new Item.Properties().tab(BMachines.TAB)));

    public static final RegistryObject<Item> DYNANIUM_INGOT = ITEMS.register("dynanium_ingot", () ->
            new Item(new Item.Properties().tab(BMachines.TAB)));

    public static final RegistryObject<Item> DYNANIUM_CORE = ITEMS.register("dynanium_core", () ->
            new Item(new Item.Properties().tab(BMachines.TAB)));

    public static void register (IEventBus eventBus)
    {
        ITEMS.register(eventBus);
        BMachines.LOGGER.info("Registered BMachines Items!");
    }
}
