package at.petrak.minerslung.datagen;

import at.petrak.minerslung.common.blocks.ModBlocks;
import at.petrak.paucal.api.datagen.PaucalLootTableProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.Map;

public class ModLootTablesProvider extends PaucalLootTableProvider {
    public ModLootTablesProvider(DataGenerator pGenerator) {
        super(pGenerator);
    }

    @Override
    protected void makeLootTables(Map<Block, LootTable.Builder> map) {
        dropSelfTable(map, ModBlocks.SAFETY_LANTERN, ModBlocks.SIGNAL_TORCH);

        var torchPool = LootPool.lootPool()
            .setRolls(ConstantValue.exactly(1.0F))
            .add(LootItem.lootTableItem(ModBlocks.SIGNAL_TORCH.get()));
        map.put(ModBlocks.WALL_SIGNAL_TORCH.get(), LootTable.lootTable().withPool(torchPool));
    }
}
