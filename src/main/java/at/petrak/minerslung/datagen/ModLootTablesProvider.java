package at.petrak.minerslung.datagen;

import at.petrak.minerslung.common.blocks.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Blocks;

public class ModLootTablesProvider extends BaseLootTableProvider {
    public ModLootTablesProvider(DataGenerator pGenerator) {
        super(pGenerator);
    }

    @Override
    protected void addTables() {
        lootTables.put(ModBlocks.SAFETY_LANTERN.get(),
            createSimpleTable("safety_lantern", ModBlocks.SAFETY_LANTERN.get()));
        lootTables.put(ModBlocks.SIGNAL_TORCH.get(), createSimpleTable("signal_torch", Blocks.TORCH));
        lootTables.put(ModBlocks.WALL_SIGNAL_TORCH.get(), createSimpleTable("wall_signal_torch", Blocks.TORCH));
    }
}
