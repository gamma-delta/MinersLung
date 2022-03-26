package at.petrak.minerslung.datagen;

import at.petrak.minerslung.MinersLungMod;
import at.petrak.minerslung.common.items.ModItems;
import at.petrak.paucal.api.lootmod.PaucalAddItemModifier;
import at.petrak.paucal.api.lootmod.PaucalLootMods;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.LootTableIdCondition;

public class ModLootMods extends GlobalLootModifierProvider {
    public ModLootMods(DataGenerator gen) {
        super(gen, MinersLungMod.MOD_ID);
    }

    @Override
    protected void start() {
        add("soulfire_bottle_buried", PaucalLootMods.ADD_ITEM.get(), new PaucalAddItemModifier(
            ModItems.SOULFIRE_BOTTLE.get(), UniformGenerator.between(3, 10),
            new ResourceLocation("minecraft:chests/buried_treasure")
        ));
        add("soulfire_bottle_shipwreck", PaucalLootMods.ADD_ITEM.get(), new PaucalAddItemModifier(
            ModItems.SOULFIRE_BOTTLE.get(), UniformGenerator.between(1, 5),
            new LootItemCondition[]{LootTableIdCondition.builder(
                new ResourceLocation("minecraft:chests/shipwreck_treasure")).build(),
                LootItemRandomChanceCondition.randomChance(0.5f).build(),
            }
        ));
        add("soulfire_bottle_big_ruin", PaucalLootMods.ADD_ITEM.get(), new PaucalAddItemModifier(
            ModItems.SOULFIRE_BOTTLE.get(), UniformGenerator.between(3, 8),
            new LootItemCondition[]{LootTableIdCondition.builder(
                new ResourceLocation("minecraft:chests/underwater_ruin_big")).build(),
                LootItemRandomChanceCondition.randomChance(0.8f).build(),
            }
        ));
        add("soulfire_bottle_big_ruin", PaucalLootMods.ADD_ITEM.get(), new PaucalAddItemModifier(
            ModItems.SOULFIRE_BOTTLE.get(), UniformGenerator.between(1, 3),
            new LootItemCondition[]{LootTableIdCondition.builder(
                new ResourceLocation("minecraft:chests/underwater_ruin_small")).build(),
                LootItemRandomChanceCondition.randomChance(0.3f).build(),
            }
        ));
        add("safety_lantern_dungeon", PaucalLootMods.ADD_ITEM.get(), new PaucalAddItemModifier(
            ModItems.SAFETY_LANTERN.get(), ConstantValue.exactly(1),
            new LootItemCondition[]{LootTableIdCondition.builder(
                new ResourceLocation("minecraft:chests/simple_dungeon")).build(),
                LootItemRandomChanceCondition.randomChance(0.3f).build(),
            }
        ));
        add("safety_lantern_mineshaft", PaucalLootMods.ADD_ITEM.get(), new PaucalAddItemModifier(
            ModItems.SAFETY_LANTERN.get(), UniformGenerator.between(1, 2),
            new LootItemCondition[]{LootTableIdCondition.builder(
                new ResourceLocation("minecraft:chests/abandoned_mineshaft")).build(),
                LootItemRandomChanceCondition.randomChance(0.7f).build(),
            }
        ));
        add("safety_lantern_stronghold", PaucalLootMods.ADD_ITEM.get(), new PaucalAddItemModifier(
            ModItems.SAFETY_LANTERN.get(), UniformGenerator.between(1, 2),
            new LootItemCondition[]{LootTableIdCondition.builder(
                new ResourceLocation("minecraft:chests/stronghold_corridor")).build(),
                LootItemRandomChanceCondition.randomChance(0.5f).build(),
            }
        ));
    }
}
