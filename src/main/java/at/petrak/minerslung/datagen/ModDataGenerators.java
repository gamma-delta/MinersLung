package at.petrak.minerslung.datagen;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

public class ModDataGenerators {
    @SubscribeEvent
    public static void generateData(GatherDataEvent evt) {
        var gen = evt.getGenerator();
        var efh = evt.getExistingFileHelper();
        if (evt.includeClient()) {
            gen.addProvider(new ModItemModels(gen, efh));
            gen.addProvider(new ModBlockModels(gen, efh));
        }
        if (evt.includeServer()) {
            gen.addProvider(new ModBlockTagsProvider(gen, efh));
            gen.addProvider(new ModLootTablesProvider(gen));
            gen.addProvider(new ModLootMods(gen));
            gen.addProvider(new ModAdvancementProvider(gen, efh));
            gen.addProvider(new ModCraftingRecipes(gen));
        }
    }
}
