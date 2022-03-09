package at.petrak.minerslung.common.breath;

import at.petrak.minerslung.MinersLungConfig;
import at.petrak.minerslung.common.advancement.AirProtectionSource;
import at.petrak.minerslung.common.advancement.AirSource;
import at.petrak.minerslung.common.capability.ModCapabilities;
import at.petrak.minerslung.common.items.ModItems;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class AirHelper {
    /**
     * Get the "actual" air quality of the location.
     */
    public static Pair<AirQualityLevel, AirSource> getO2LevelFromLocation(Vec3 position, Level world) {
        // do we allow soul campfires to override being underwater?
        if (isInFluid(position, world)) {
            return new Pair<>(AirQualityLevel.RED, AirSource.FLUID);
        }

        // Let's throw the player a bone and say the best air quality wins
        Pair<AirQualityLevel, AirSource> bestAirBubbleQuality = null;
        var centerChunkPos = new ChunkPos(new BlockPos(position));
        // max radius for a campfire is 32, so that means we check two chunks on each side.
        var chunkRadius = 2;
        for (int cdx = -chunkRadius; cdx <= chunkRadius; cdx++) {
            for (int cdz = -chunkRadius; cdz <= chunkRadius; cdz++) {
                var chunkpos = new ChunkPos(centerChunkPos.x + cdx, centerChunkPos.z + cdz);
                var chunk = world.getChunkSource().getChunkNow(chunkpos.x, chunkpos.z);
                if (chunk == null) {
                    continue;
                }
                var maybeCap = chunk.getCapability(ModCapabilities.AIR_BUBBLE_POSITIONS).resolve();
                if (maybeCap.isEmpty()) {
                    continue;
                }
                var cap = maybeCap.get();
                for (var pos : cap.entries.keySet()) {
                    var entry = cap.entries.get(pos);
                    if (bestAirBubbleQuality == null || !bestAirBubbleQuality.getFirst()
                        .bubbleBeats(entry.airQuality())) {
                        var bs = world.getBlockState(pos);
                        if (AirBubbleTracker.canProjectAirBubble(bs)) {
                            var distSq = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5).distanceToSqr(
                                position);
                            if (distSq < entry.radius() * entry.radius()) {
                                AirSource source = null;
                                if (bs.is(Blocks.SOUL_CAMPFIRE) || bs.is(Blocks.SOUL_FIRE) || bs.is(
                                    Blocks.SOUL_TORCH) || bs.is(Blocks.SOUL_WALL_TORCH)) {
                                    source = AirSource.SOUL;
                                } else if (bs.is(Blocks.LAVA)) {
                                    source = AirSource.LAVA;
                                } // else uh oh
                                if (source != null) {
                                    bestAirBubbleQuality = new Pair<>(entry.airQuality(), source);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (bestAirBubbleQuality != null) {
            return bestAirBubbleQuality;
        }

        var dim = world.dimension().location();
        var quality = MinersLungConfig.getAirQualityAtLevelByDimension(dim, (int) Math.round(position.y));
        return new Pair<>(quality, AirSource.DIMENSION);
    }

    public static boolean isInFluid(Vec3 position, Level world) {
        var blockAtEyes = world.getBlockState(new BlockPos(position));
        return !blockAtEyes.getFluidState().isEmpty() && !blockAtEyes.is(Blocks.BUBBLE_COLUMN);
    }

    public static AirProtectionSource getProtectionFromYellow(LivingEntity entity) {
        var red = getProtectionFromRed(entity);
        if (red != null) {
            return red;
        }

        var armorItems = entity.getArmorSlots();
        for (var item : armorItems) {
            if (item.is(ModItems.RESPIRATOR.get())) {
                return AirProtectionSource.RESPIRATOR;
            }
        }

        return AirProtectionSource.NONE;
    }

    public static AirProtectionSource getProtectionFromRed(LivingEntity entity) {
        if (canAlwaysBreathe(entity)) {
            return AirProtectionSource.INHERENT;
        }

        if (entity.hasEffect(MobEffects.WATER_BREATHING)) {
            return AirProtectionSource.WATER_BREATHING;
        }

        var maybeCap = entity.getCapability(ModCapabilities.IS_PROTECTED_FROM_AIR).resolve();
        if (maybeCap.isPresent()) {
            var cap = maybeCap.get();
            if (cap.isUsingBladder) {
                return AirProtectionSource.BLADDER;
            }
        }

        return AirProtectionSource.NONE;
    }

    public static boolean canAlwaysBreathe(LivingEntity entity) {
        if (entity.canBreatheUnderwater()
            || (entity instanceof Player player && player.getAbilities().invulnerable)) {
            return true;
        }
        var alwaysOkEntities = MinersLungConfig.alwaysBreathingEntities.get();
        return alwaysOkEntities.contains(entity.getEncodeId());
    }
}
