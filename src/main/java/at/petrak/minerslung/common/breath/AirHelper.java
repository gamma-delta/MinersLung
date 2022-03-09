package at.petrak.minerslung.common.breath;

import at.petrak.minerslung.MinersLungConfig;
import at.petrak.minerslung.common.capability.ModCapabilities;
import at.petrak.minerslung.common.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.RegistryObject;
import org.antlr.v4.runtime.misc.Triple;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class AirHelper {
    /**
     * Get the "actual" air quality of the location.
     */
    public static AirQualityLevel getO2LevelFromLocation(Vec3 position, Level world) {
        // do we allow soul campfires to override being underwater?
        if (isUnderwater(position, world)) {
            return AirQualityLevel.RED;
        }

        // Let's throw the player a bone and say the best air quality wins
        AirQualityLevel bestAirBubbleQuality = null;
        for (var pair : AIR_PROJECTORS) {
            var poi = pair.a.get();
            var airQuality = pair.b;
            var radius = pair.c.get();
            if (world instanceof ServerLevel slevel) {
                
            }
        }
        if (bestAirBubbleQuality != null) {
            return bestAirBubbleQuality;
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

        var cap = entity.getCapability(ModCapabilities.IS_PROTECTED_FROM_AIR).resolve();
        if (cap.isPresent() && cap.get().isProtected()) {
            return true;
        }

        return false;
    }

    private static List<Triple<RegistryObject<PoiType>, AirQualityLevel, ForgeConfigSpec.DoubleValue>> AIR_PROJECTORS = Arrays.asList(
        new Triple<>(ModPointsOfInterest.SOUL_TORCH, AirQualityLevel.GREEN, MinersLungConfig.soulTorchRange),
        new Triple<>(ModPointsOfInterest.SOUL_FIRE, AirQualityLevel.GREEN, MinersLungConfig.soulFireRange),
        new Triple<>(ModPointsOfInterest.SOUL_CAMPFIRE, AirQualityLevel.GREEN, MinersLungConfig.soulCampfireRange)
    );
}
