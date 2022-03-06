package at.petrak.minerslung.common.items;

import at.petrak.minerslung.MinersLungMod;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MinersLungMod.MOD_ID);

    public static RegistryObject<RespiratorItem> RESPIRATOR = ITEMS.register("respirator",
        () -> new RespiratorItem(new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_COMBAT)));
}
