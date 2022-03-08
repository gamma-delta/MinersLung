package at.petrak.minerslung.datagen;

import at.petrak.minerslung.common.advancement.BreatheAirTrigger;
import at.petrak.minerslung.common.breath.AirQualityLevel;
import at.petrak.minerslung.common.items.ModItems;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class ModCraftingRecipes extends RecipeProvider {
    public ModCraftingRecipes(DataGenerator pGenerator) {
        super(pGenerator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> recipes) {
        var yellowTrigger = new BreatheAirTrigger.Instance(EntityPredicate.Composite.ANY, AirQualityLevel.YELLOW, false,
            true,
            true);
        ShapedRecipeBuilder.shaped(ModItems.RESPIRATOR.get())
            .define('P', Items.PAPER)
            .define('S', Items.STRING)
            .define('C', Items.CHARCOAL)
            .pattern(" S ")
            .pattern("P P")
            .pattern("PCP")
            .unlockedBy("namesarehard",
                yellowTrigger)
            .save(recipes);

        ShapedRecipeBuilder.shaped(ModItems.AIR_BLADDER.get())
            .define('L', Items.LEATHER)
            .define('S', Items.STRING)
            .pattern(" LS")
            .pattern("L L")
            .pattern(" L ")
            .unlockedBy("namesarehard",
                yellowTrigger)
            .save(recipes);

        ShapedRecipeBuilder.shaped(ModItems.SAFETY_LANTERN.get())
            .define('C', Ingredient.m_204132_(Tags.Items.INGOTS_COPPER))
            .define('T', Items.REDSTONE_TORCH)
            .pattern("CCC")
            .pattern("CTC")
            .pattern("CCC")
            .unlockedBy("namesarehard", yellowTrigger)
            .save(recipes);

        ShapedRecipeBuilder.shaped(ModItems.PRISMARINE_BOTTLE.get(), 3)
            .define('G', Ingredient.m_204132_(Tags.Items.GLASS))
            .define('P', Ingredient.m_204132_(Tags.Items.GEMS_PRISMARINE))
            .pattern("GPG")
            .pattern(" G ")
            .unlockedBy("namesarehard", yellowTrigger)
            .save(recipes, "prismarine_bottle_from_glass");

        ShapelessRecipeBuilder.shapeless(ModItems.PRISMARINE_BOTTLE.get(), 3)
            .requires(Items.GLASS_BOTTLE, 3)
            .requires(Ingredient.m_204132_(Tags.Items.GEMS_PRISMARINE))
            .unlockedBy("namesarehard", yellowTrigger)
            .save(recipes, "prismarine_bottle");

    }
}
