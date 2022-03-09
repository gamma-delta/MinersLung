package at.petrak.minerslung.common.breath;

import at.petrak.minerslung.MinersLungConfig;
import at.petrak.minerslung.common.capability.ModCapabilities;
import at.petrak.minerslung.common.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
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
        // do we allow soul campfires to override being underwater?
        if (isUnderwater(position, world)) {
            return AirQualityLevel.RED;
        }

        // Let's throw the player a bone and say the best air quality wins
        AirQualityLevel bestAirBubbleQuality = null;
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
                    if (bestAirBubbleQuality == null || !bestAirBubbleQuality.bubbleBeats(entry.airQuality())) {
                        var blockThere = world.getBlockState(pos);
                        if (AirBubbleTracker.canProjectAirBubble(blockThere)) {
                            var distSq = new Vec3(pos.getX(), pos.getY(), pos.getZ()).distanceToSqr(position);
                            if (distSq < (entry).radius() * entry.radius()) {
                                bestAirBubbleQuality = entry.airQuality();
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
        return MinersLungConfig.getAirQualityAtLevelByDimension(dim, (int) Math.round(position.y));
    }

    public static boolean isUnderwater(Vec3 position, Level world) {
        var blockAtEyes = world.getBlockState(new BlockPos(position));
        return !blockAtEyes.getFluidState().isEmpty() && !blockAtEyes.is(Blocks.BUBBLE_COLUMN);
    }

    /**
     * @return `null` if not protected; `Optional.empty` if protected without a respirator somehow;
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
        if (canAlwaysBreathe(entity)) {
            return true;
        }

        var cap = entity.getCapability(ModCapabilities.IS_PROTECTED_FROM_AIR).resolve();
        if (cap.isPresent() && cap.get().isProtected()) {
            return true;
        }

        return false;
    }

    public static boolean canAlwaysBreathe(LivingEntity entity) {
        if (entity.canBreatheUnderwater()
            || entity.hasEffect(MobEffects.WATER_BREATHING)
            || (entity instanceof Player player && player.getAbilities().invulnerable)) {
            return true;
        }
        var alwaysOkEntities = MinersLungConfig.alwaysBreathingEntities.get();
        return alwaysOkEntities.contains(entity.getEncodeId());
    }
}
