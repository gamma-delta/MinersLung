package at.petrak.minerslung.datagen.lootmods;

import at.petrak.minerslung.MinersLungMod;
import at.petrak.minerslung.common.items.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootTableIdCondition;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModLootMods extends GlobalLootModifierProvider {
    public static final DeferredRegister<GlobalLootModifierSerializer<?>> LOOT_MODS = DeferredRegister.create(
        ForgeRegistries.LOOT_MODIFIER_SERIALIZERS, MinersLungMod.MOD_ID);
    private static final RegistryObject<PrismarineBottleSer> PRISMARINE_BOTTLE = LOOT_MODS.register(
        "prismarine_bottle", PrismarineBottleSer::new);
    private static final RegistryObject<SafetyLanternSer> SAFETY_LANTERN = LOOT_MODS.register(
        "safety_lantern", SafetyLanternSer::new);


    public ModLootMods(DataGenerator gen) {
        super(gen, MinersLungMod.MOD_ID);
    }

    @Override
    protected void start() {
        add("prismarine_bottle_buried", PRISMARINE_BOTTLE.get(), new GenericAddItemModifier(new LootItemCondition[]{
            LootTableIdCondition.builder(new ResourceLocation("minecraft:chests/buried_treasure")).build()
        }, null, 1.0f, 3, 10));
        add("prismarine_bottle_shipwreck", PRISMARINE_BOTTLE.get(), new GenericAddItemModifier(new LootItemCondition[]{
            LootTableIdCondition.builder(new ResourceLocation("minecraft:chests/shipwreck_treasure")).build()
        }, null, 0.5f, 1, 5));
        add("prismarine_bottle_big_ruin", PRISMARINE_BOTTLE.get(), new GenericAddItemModifier(new LootItemCondition[]{
            LootTableIdCondition.builder(new ResourceLocation("minecraft:chests/underwater_ruin_big")).build()
        }, null, 0.8f, 3, 8));
        add("prismarine_bottle_small_ruin", PRISMARINE_BOTTLE.get(), new GenericAddItemModifier(new LootItemCondition[]{
            LootTableIdCondition.builder(new ResourceLocation("minecraft:chests/underwater_ruin_small")).build()
        }, null, 0.3f, 1, 3));

        add("safety_lantern_dungeon", SAFETY_LANTERN.get(), new GenericAddItemModifier(new LootItemCondition[]{
            LootTableIdCondition.builder(new ResourceLocation("minecraft:chests/simple_dungeon")).build()
        }, null, 0.3f, 1, 1));
        add("safety_lantern_mineshaft", SAFETY_LANTERN.get(), new GenericAddItemModifier(new LootItemCondition[]{
            LootTableIdCondition.builder(new ResourceLocation("minecraft:chests/abandoned_mineshaft")).build()
        }, null, 0.7f, 1, 2));
        add("safety_lantern_stronghold", SAFETY_LANTERN.get(), new GenericAddItemModifier(new LootItemCondition[]{
            LootTableIdCondition.builder(new ResourceLocation("minecraft:chests/stronghold_corridor")).build()
        }, null, 0.5f, 1, 2));
    }

    private static class PrismarineBottleSer extends GenericAddItemModifier.Serializer {
        @Override
        public ItemStack getItem() {
            return new ItemStack(ModItems.SOULFIRE_BOTTLE.get());
        }
    }

    private static class SafetyLanternSer extends GenericAddItemModifier.Serializer {
        @Override
        public ItemStack getItem() {
            return new ItemStack(ModItems.SAFETY_LANTERN.get());
        }
    }
}
