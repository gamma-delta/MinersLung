package at.petrak.minerslung.common.items;

import at.petrak.minerslung.MinersLungMod;
import at.petrak.minerslung.common.blocks.ModBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MinersLungMod.MOD_ID);

    public static RegistryObject<RespiratorItem> RESPIRATOR = ITEMS.register("respirator",
        () -> new RespiratorItem(new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_TOOLS)));
    public static RegistryObject<AirBladderItem> AIR_BLADDER = ITEMS.register("air_bladder",
        () -> new AirBladderItem(
            new Item.Properties().stacksTo(1).durability(300).defaultDurability(0).tab(CreativeModeTab.TAB_TOOLS)));
    public static RegistryObject<PrismarineBottleItem> PRISMARINE_BOTTLE = ITEMS.register("prismarine_bottle",
        () -> new PrismarineBottleItem(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS)));

    public static RegistryObject<BlockItem> SAFETY_LANTERN = ITEMS.register("safety_lantern",
        () -> new BlockItem(ModBlocks.SAFETY_LANTERN.get(),
            new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)));
    public static ResourceLocation SAFETY_LANTERN_AIR_QUALITY_PRED = new ResourceLocation(MinersLungMod.MOD_ID,
        "air_quality");

    public static RegistryObject<Item> FAKE_ALWAYS_RED_LANTERN = ITEMS.register("fake_always_red_lantern",
        () -> new Item(new Item.Properties()));
    public static RegistryObject<Item> FAKE_ALWAYS_YELLOW_LANTERN = ITEMS.register("fake_always_yellow_lantern",
        () -> new Item(new Item.Properties()));
    public static RegistryObject<Item> FAKE_ALWAYS_GREEN_LANTERN = ITEMS.register("fake_always_green_lantern",
        () -> new Item(new Item.Properties()));
    public static RegistryObject<Item> FAKE_RAINBOW_LANTERN = ITEMS.register("fake_rainbow_lantern",
        () -> new Item(new Item.Properties()));
}
