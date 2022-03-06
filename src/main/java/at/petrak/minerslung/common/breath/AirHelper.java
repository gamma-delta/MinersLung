package at.petrak.minerslung.common.breath;

import at.petrak.minerslung.MinersLungConfig;
import at.petrak.minerslung.common.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AirHelper {
    public static OxygenLevel getO2LevelFromLocation(LivingEntity entity) {
        if (entity.canBreatheUnderwater() || entity instanceof Enemy) {
            return OxygenLevel.GREEN;
        }
        var blockAtEyes = entity.level.getBlockState(new BlockPos(entity.getX(), entity.getEyeY(), entity.getZ()));
        if (!blockAtEyes.getFluidState().isEmpty() && !blockAtEyes.is(Blocks.BUBBLE_COLUMN)) {
            return OxygenLevel.RED;
        }
        var dim = entity.level.dimension().location().toString();
        if (MinersLungConfig.noOxygenDimensions.get().contains(dim)) {
            return OxygenLevel.RED;
        } else if (MinersLungConfig.poorOxygenDimensions.get().contains(dim)) {
            return OxygenLevel.YELLOW;
        } else if (MinersLungConfig.getPoorOxygenThresholdDims().containsKey(dim)) {
            var depth = MinersLungConfig.getPoorOxygenThresholdDims().get(dim);
            if (entity.getY() < depth) {
                return OxygenLevel.YELLOW;
            }
        }
        return OxygenLevel.GREEN;
    }

    /**
     * @return `null` if not protected; `Optional.empty` if protected without a respirator;
     * `Optional.of` if protected with a respirator
     */
    public static @Nullable Optional<ItemStack> getProtectionFromYellow(LivingEntity entity) {
        if (isProtectedFromRed(entity)) {
            return Optional.empty();
        }

        var armorItems = entity.getArmorSlots();
        for (var item : armorItems) {
            if (item.is(ModItems.RESPIRATOR.get())) {
                return Optional.of(item);
            }
        }

        return null;
    }

    public static boolean isProtectedFromRed(LivingEntity entity) {
        return entity instanceof ArmorStand || entity.canBreatheUnderwater() || entity instanceof Enemy || entity.hasEffect(
            MobEffects.WATER_BREATHING);
    }
}
