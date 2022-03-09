package at.petrak.minerslung.common.breath;

import at.petrak.minerslung.MinersLungConfig;
import at.petrak.minerslung.MinersLungMod;
import at.petrak.minerslung.common.capability.CapAirBubblePositions;
import at.petrak.minerslung.common.capability.ModCapabilities;
import at.petrak.minerslung.common.network.ModMessages;
import at.petrak.minerslung.common.network.MsgSyncBubblesSyn;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class AirBubbleTracker {
    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent evt) {
        var bs = evt.getPlacedBlock();
        if (canActuallyProjectAirBubbleOnPlace(bs)) {
            var relevantRadius = 0.0;
            if (bs.is(Blocks.SOUL_CAMPFIRE)) {
                relevantRadius = MinersLungConfig.soulCampfireRange.get();
            } else if (bs.is(Blocks.SOUL_FIRE)) {
                relevantRadius = MinersLungConfig.soulFireRange.get();
            } else if (bs.is(Blocks.SOUL_TORCH) || bs.is(Blocks.SOUL_WALL_TORCH)) {
                relevantRadius = MinersLungConfig.soulTorchRange.get();
            }
            if (relevantRadius != 0.0) {
                var chunkPos = new ChunkPos(evt.getPos());
                var chunk = evt.getWorld().getChunkSource().getChunkNow(chunkPos.x, chunkPos.z);
                if (chunk != null) {
                    var maybeCap = chunk.getCapability(ModCapabilities.AIR_BUBBLE_POSITIONS);
                    double finalRelevantRadius = relevantRadius;
                    maybeCap.ifPresent(cap -> {
                        var clobbered =
                            cap.entries.put(evt.getPos(),
                                new CapAirBubblePositions.Entry(AirQualityLevel.GREEN, finalRelevantRadius));
                        if (clobbered != null) {
                            MinersLungMod.LOGGER.warn("Clobbered air bubble at {}: {}", evt.getPos(), clobbered);
                        }
                    });
                }
            }
        }
    }

    @SubscribeEvent
    public static void onBlockRemoved(BlockEvent.BreakEvent evt) {
        var bs = evt.getWorld().getBlockState(evt.getPos());
        if (bs.is(Blocks.SOUL_CAMPFIRE)) {
            var chunkPos = new ChunkPos(evt.getPos());
            var chunk = evt.getWorld().getChunkSource().getChunkNow(chunkPos.x, chunkPos.z);
            if (chunk != null) {
                var maybeCap = chunk.getCapability(ModCapabilities.AIR_BUBBLE_POSITIONS);
                maybeCap.ifPresent(cap -> {
                    var removed = cap.entries.remove(evt.getPos());
                    if (removed == null) {
                        MinersLungMod.LOGGER.warn("Didn't remove any air bubbles at {}", evt.getPos());
                    }
                });
            }
        }
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load evt) {
        if (evt.getWorld().isClientSide()) {
            var pos = evt.getChunk().getPos();
            ModMessages.getNetwork().sendToServer(new MsgSyncBubblesSyn(pos.x, pos.z));
        }
    }

    public static boolean canActuallyProjectAirBubble(BlockState bs) {
        return (bs.is(Blocks.SOUL_CAMPFIRE) && bs.getValue(CampfireBlock.LIT))
            || bs.is(Blocks.SOUL_FIRE)
            || bs.is(Blocks.SOUL_TORCH)
            || bs.is(Blocks.SOUL_WALL_TORCH);
    }

    public static boolean canActuallyProjectAirBubbleOnPlace(BlockState bs) {
        return bs.is(Blocks.SOUL_CAMPFIRE)
            || bs.is(Blocks.SOUL_FIRE)
            || bs.is(Blocks.SOUL_TORCH)
            || bs.is(Blocks.SOUL_WALL_TORCH);
    }
}
