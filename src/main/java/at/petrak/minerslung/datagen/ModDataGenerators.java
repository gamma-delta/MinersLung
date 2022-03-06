package at.petrak.minerslung.datagen;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

public class ModDataGenerators {
    @SubscribeEvent
    public static void generateData(GatherDataEvent evt) {
        var gen = evt.getGenerator();
        var efh = evt.getExistingFileHelper();
        if (evt.includeClient()) {
            gen.addProvider(new ModBlockModels(gen, efh));
        }
    }
}
