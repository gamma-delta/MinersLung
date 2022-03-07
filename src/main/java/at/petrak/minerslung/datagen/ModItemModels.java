package at.petrak.minerslung.datagen;

import at.petrak.minerslung.MinersLungMod;
import at.petrak.minerslung.common.items.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemModels extends ItemModelProvider {
    public ModItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, MinersLungMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(ModItems.RESPIRATOR.get());
    }

    public void simpleItem(Item item) {
        simpleItem(item.getRegistryName());
    }

    public void simpleItem(ResourceLocation path) {
        singleTexture(path.getPath(), new ResourceLocation("item/generated"),
            "layer0", modLoc("item/" + path.getPath()));
    }

    public void brandishedItem(Item item) {
        brandishedItem(item.getRegistryName());
    }

    public void brandishedItem(ResourceLocation path) {
        singleTexture(path.getPath(), new ResourceLocation("item/handheld"),
            "layer0", modLoc("item/" + path.getPath()));
    }
}
