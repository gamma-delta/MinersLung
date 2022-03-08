package at.petrak.minerslung.datagen.lootmods;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public class GenericAddItemModifier extends LootModifier {
    private final float chance;
    private final int min, max;

    private final Supplier<ItemStack> stack;

    public GenericAddItemModifier(LootItemCondition[] conds, Supplier<ItemStack> stack, float chance, int min,
        int max) {
        super(conds);
        this.stack = stack;
        this.chance = chance;
        this.min = min;
        this.max = max;
    }

    @NotNull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        var rand = context.getRandom();
        if (rand.nextFloat() < chance) {
            var count = rand.nextInt(max - min + 1) + min;
            var newLoot = this.stack.get();
            newLoot.setCount(count);
            generatedLoot.add(newLoot);
        }

        return generatedLoot;
    }

    public abstract static class Serializer extends GlobalLootModifierSerializer<GenericAddItemModifier> {
        @Contract(pure = true)
        public abstract ItemStack getItem();

        @Override
        public GenericAddItemModifier read(ResourceLocation location, JsonObject obj,
            LootItemCondition[] conds) {
            var chance = GsonHelper.getAsFloat(obj, "chance");
            var min = GsonHelper.getAsInt(obj, "min");
            var max = GsonHelper.getAsInt(obj, "max");

            return new GenericAddItemModifier(conds, this::getItem, chance, min, max);
        }

        @Override
        public JsonObject write(GenericAddItemModifier instance) {
            var obj = makeConditions(instance.conditions);
            obj.addProperty("chance", instance.chance);
            obj.addProperty("min", instance.min);
            obj.addProperty("max", instance.max);
            return obj;
        }
    }
}
