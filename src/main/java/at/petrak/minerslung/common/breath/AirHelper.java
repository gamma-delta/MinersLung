package at.petrak.minerslung.common.breath;

import at.petrak.minerslung.MinersLungConfig;
import at.petrak.minerslung.common.capability.ModCapabilities;
import at.petrak.minerslung.common.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AirHelper {
    /**
     * Get the "actual" air quality of the location.
     */
    public static AirQualityLevel getO2LevelFromLocation(Vec3 position, Level world) {
        if (isUnderwater(position, world)) {
            return AirQualityLevel.RED;
        }
        var dim = world.dimension().location().toString();
        if (MinersLungConfig.noOxygenDimensions.get().contains(dim)) {
            return AirQualityLevel.RED;
        } else if (MinersLungConfig.poorOxygenDimensions.get().contains(dim)) {
            return AirQualityLevel.YELLOW;
        } else if (MinersLungConfig.getPoorOxygenThresholdDims().containsKey(dim)) {
            var depth = MinersLungConfig.getPoorOxygenThresholdDims().get(dim);
            if (position.y < depth) {
                return AirQualityLevel.YELLOW;
            }
        }
        return AirQualityLevel.GREEN;
    }

    public static boolean isUnderwater(Vec3 position, Level world) {
        var blockAtEyes = world.getBlockState(new BlockPos(position));
        return !blockAtEyes.getFluidState().isEmpty() && !blockAtEyes.is(Blocks.BUBBLE_COLUMN);
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
        var normalProt = entity instanceof ArmorStand
            || entity.canBreatheUnderwater()
            || entity instanceof Enemy
            || entity.hasEffect(MobEffects.WATER_BREATHING);
        if (normalProt) {
            return true;
        }

        var cap = entity.getCapability(ModCapabilities.IS_USING_BLADDER).resolve();
        if (cap.isPresent() && cap.get().isUsingBladder) {
            return true;
        }

        return false;
    }
}
