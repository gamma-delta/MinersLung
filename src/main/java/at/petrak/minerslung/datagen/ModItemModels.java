package at.petrak.minerslung.datagen;

import at.petrak.minerslung.MinersLungMod;
import at.petrak.minerslung.common.breath.AirQualityLevel;
import at.petrak.minerslung.common.items.ModItems;
import at.petrak.paucal.api.datagen.PaucalItemModelProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemModels extends PaucalItemModelProvider {
    public ModItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, MinersLungMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(ModItems.RESPIRATOR.get());
        simpleItem(ModItems.SOULFIRE_BOTTLE.get());

        for (int i = 0; i <= 2; i++) {
            var name = "air_bladder_" + i;
            simpleItem(modLoc(name));
            var prop = (float) i / 3f;
            getBuilder(ModItems.AIR_BLADDER.getId().getPath())
                .override()
                .predicate(new ResourceLocation("damage"), prop)
                .model(new ModelFile.UncheckedModelFile(modLoc("item/" + name)))
                .end();
        }

        AirQualityLevel[] aqs = AirQualityLevel.values();
        for (int i = 0; i < aqs.length; i++) {
            AirQualityLevel aq = aqs[3 - i];
            var name = "lantern_" + aq.getSerializedName();
            simpleItem(modLoc(name));
            var texPath = modLoc("item/" + name);
            getBuilder(ModItems.SAFETY_LANTERN.get().getRegistryName().getPath())
                .override()
                .predicate(ModItems.SAFETY_LANTERN_AIR_QUALITY_PRED, i)
                .model(new ModelFile.UncheckedModelFile(texPath))
                .end();

            singleTexture("fake_always_" + aq.getSerializedName() + "_lantern", new ResourceLocation("item/generated"),
                "layer0", texPath);
        }
        singleTexture("fake_rainbow_lantern", new ResourceLocation("item/generated"),
            "layer0", modLoc("item/lantern_rainbow"));
    }
}
