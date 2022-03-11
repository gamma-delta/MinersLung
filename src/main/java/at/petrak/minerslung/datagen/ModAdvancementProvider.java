package at.petrak.minerslung.datagen;

import at.petrak.minerslung.MinersLungMod;
import at.petrak.minerslung.common.advancement.*;
import at.petrak.minerslung.common.breath.AirQualityLevel;
import at.petrak.minerslung.common.items.ModItems;
import com.mojang.datafixers.util.Either;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.EnumSet;
import java.util.function.Consumer;

public class ModAdvancementProvider extends AdvancementProvider {
    public ModAdvancementProvider(DataGenerator generatorIn,
        ExistingFileHelper fileHelperIn) {
        super(generatorIn, fileHelperIn);
    }

    @Override
    protected void registerAdvancements(Consumer<Advancement> advancements, ExistingFileHelper fileHelper) {
        var root = Advancement.Builder.advancement()
            .display(simpleWithBackground(ModItems.FAKE_ALWAYS_RED_LANTERN.get(), "root", FrameType.TASK,
                new ResourceLocation("textures/block/deepslate.png")))
            .addCriterion("bad_air",
                new BreatheAirTrigger.Instance(EntityPredicate.Composite.ANY,
                    EnumSet.of(AirQualityLevel.YELLOW, AirQualityLevel.RED), null, null))
            .save(advancements, prefix("root"));

        var bladder = Advancement.Builder.advancement()
            .display(simple(ModItems.AIR_BLADDER.get(), "air_bladder", FrameType.TASK))
            .parent(root)
            .addCriterion("bad_air",
                new BreatheAirTrigger.Instance(EntityPredicate.Composite.ANY,
                    EnumSet.of(AirQualityLevel.YELLOW, AirQualityLevel.RED), null,
                    Either.left(AirProtectionSource.BLADDER)))
            .save(advancements, prefix("air_bladder"));

        var soul = Advancement.Builder.advancement()
            .display(simple(Items.SOUL_CAMPFIRE, "soul", FrameType.TASK))
            .parent(bladder)
            .addCriterion("bad_air",
                new BreatheAirTrigger.Instance(EntityPredicate.Composite.ANY, EnumSet.allOf(AirQualityLevel.class),
                    AirSource.SOUL, null))
            .save(advancements, prefix("soul"));

        Advancement.Builder.advancement()
            .display(simple(ModItems.SOULFIRE_BOTTLE.get(), "soulfire_bottle", FrameType.GOAL))
            .parent(soul)
            .addCriterion("on_use",
                new UseSoulfireBecauseItDoesntTriggerVanillaForSomeReasonTrigger.Instance(
                    EntityPredicate.Composite.ANY))
            .save(advancements, prefix("soulfire_bottle"));

        var protectYellow = Advancement.Builder.advancement()
            .display(simple(ModItems.RESPIRATOR.get(), "protection_from_yellow", FrameType.TASK))
            .parent(root)
            .addCriterion("protecc",
                new BreatheAirTrigger.Instance(EntityPredicate.Composite.ANY,
                    EnumSet.of(AirQualityLevel.YELLOW, AirQualityLevel.RED), null,
                    null))
            .save(advancements, prefix("protection_from_yellow"));

        Advancement.Builder.advancement()
            .display(new DisplayInfo(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER_BREATHING),
                new TranslatableComponent("advancement." + MinersLungMod.MOD_ID + ":protection_from_red"),
                new TranslatableComponent("advancement." + MinersLungMod.MOD_ID + ":protection_from_red.desc"),
                null, FrameType.GOAL, true, true, false))
            .parent(protectYellow)
            .addCriterion("protecc",
                new BreatheAirTrigger.Instance(EntityPredicate.Composite.ANY, EnumSet.of(AirQualityLevel.RED), null,
                    Either.right(Unit.INSTANCE)))
            .save(advancements, prefix("protection_from_red"));

        var lantern = Advancement.Builder.advancement()
            .display(simple(ModItems.FAKE_ALWAYS_GREEN_LANTERN.get(), "lantern", FrameType.TASK))
            .parent(root)
            .addCriterion("place",
                new InventoryChangeTrigger.TriggerInstance(EntityPredicate.Composite.ANY, MinMaxBounds.Ints.ANY,
                    MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY,
                    new ItemPredicate[]{ItemPredicate.Builder.item().of(ModItems.SAFETY_LANTERN.get()).build()}))
            .save(advancements, prefix("lantern"));

        Advancement.Builder.advancement()
            .display(simple(ModItems.FAKE_RAINBOW_LANTERN.get(), "disco_lantern", FrameType.TASK))
            .parent(lantern)
            .addCriterion("red",
                new ItemUsedOnBlockTrigger.TriggerInstance(EntityPredicate.Composite.ANY, LocationPredicate.ANY,
                    ItemPredicate.Builder.item().m_204145_(Tags.Items.DYES_RED).build()))
            .addCriterion("yellow",
                new ItemUsedOnBlockTrigger.TriggerInstance(EntityPredicate.Composite.ANY, LocationPredicate.ANY,
                    ItemPredicate.Builder.item().m_204145_(Tags.Items.DYES_YELLOW).build()))
            .addCriterion("blue",
                new ItemUsedOnBlockTrigger.TriggerInstance(EntityPredicate.Composite.ANY, LocationPredicate.ANY,
                    ItemPredicate.Builder.item().m_204145_(Tags.Items.DYES_BLUE).build()))
            .addCriterion("green",
                new ItemUsedOnBlockTrigger.TriggerInstance(EntityPredicate.Composite.ANY, LocationPredicate.ANY,
                    ItemPredicate.Builder.item().m_204145_(Tags.Items.DYES_GREEN).build()))
            .requirements(new String[][]{{"red", "yellow", "blue", "green"}})
            .save(advancements, prefix("disco_lantern"));

        Advancement.Builder.advancement()
            .display(simple(Items.TORCH, "signal_torch", FrameType.TASK))
            .parent(root)
            .addCriterion("use", new SignalificateTorchTrigger.Instance(EntityPredicate.Composite.ANY,
                LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block()
                    .of(Blocks.TORCH, Blocks.WALL_TORCH).build()).build()))
            .save(advancements, prefix("signal_torch"));
    }

    protected static DisplayInfo simple(ItemLike icon, String name, FrameType frameType) {
        return simpleWithBackground(icon, name, frameType, null);
    }

    protected static DisplayInfo simpleWithBackground(ItemLike icon, String name, FrameType frameType,
        ResourceLocation background) {
        String expandedName = "advancement." + MinersLungMod.MOD_ID + ":" + name;
        return new DisplayInfo(new ItemStack(icon.asItem()),
            new TranslatableComponent(expandedName),
            new TranslatableComponent(expandedName + ".desc"),
            background, frameType, true, true, false);
    }

    private static String prefix(String name) {
        return MinersLungMod.MOD_ID + ":" + name;
    }
}
