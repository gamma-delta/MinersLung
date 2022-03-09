package at.petrak.minerslung.common.breath;

import at.petrak.minerslung.MinersLungConfig;
import at.petrak.minerslung.MinersLungMod;
import at.petrak.minerslung.common.capability.CapAirBubblePositions;
import at.petrak.minerslung.common.capability.ModCapabilities;
import at.petrak.minerslung.common.network.ModMessages;
import at.petrak.minerslung.common.network.MsgSyncBubblesSyn;
import at.petrak.minerslung.common.network.MsgSyncSingleBubbleAck;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class AirBubbleTracker {
    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent evt) {
        var bs = evt.getPlacedBlock();
        var bubble = getPlaceBubble(bs);
        if (bubble != null) {
            if (bubble.getSecond().get() != 0.0) {
                var chunkPos = new ChunkPos(evt.getPos());
                var chunk = evt.getWorld().getChunkSource().getChunkNow(chunkPos.x, chunkPos.z);
                if (chunk != null) {
                    var maybeCap = chunk.getCapability(ModCapabilities.AIR_BUBBLE_POSITIONS);
                    double radius = bubble.getSecond().get();
                    maybeCap.ifPresent(cap -> {
                        var entry = new CapAirBubblePositions.Entry(bubble.getFirst(), radius);
                        var clobbered =
                            cap.entries.put(evt.getPos(), entry);
                        if (clobbered != null) {
                            MinersLungMod.LOGGER.warn("(server) Clobbered air bubble at {}: {}", evt.getPos(),
                                clobbered);
                        }
                        ModMessages.getNetwork()
                            .send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk),
                                new MsgSyncSingleBubbleAck(evt.getPos(), entry));
                    });
                }
            }
        }
    }

    @SubscribeEvent
    public static void onBlockRemoved(BlockEvent.BreakEvent evt) {
        var bs = evt.getWorld().getBlockState(evt.getPos());
        if (getPlaceBubble(bs) != null) {
            var chunkPos = new ChunkPos(evt.getPos());
            var chunk = evt.getWorld().getChunkSource().getChunkNow(chunkPos.x, chunkPos.z);
            if (chunk != null) {
                var maybeCap = chunk.getCapability(ModCapabilities.AIR_BUBBLE_POSITIONS);
                maybeCap.ifPresent(cap -> {
                    var removed = cap.entries.remove(evt.getPos());
                    if (removed == null) {
                        MinersLungMod.LOGGER.warn("(server) Didn't remove any air bubbles at {}", evt.getPos());
                    }
                    ModMessages.getNetwork()
                        .send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk),
                            new MsgSyncSingleBubbleAck(evt.getPos(), null));
                });
            }
        }
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load evt) {
        // this event happens only on the client
        var pos = evt.getChunk().getPos();
        ModMessages.getNetwork().sendToServer(new MsgSyncBubblesSyn(pos.x, pos.z));
    }

    /**
     * Whether the blockstate projecting a bubble actually provides that bubble.
     * <p>
     * For example soul campfires provide a blue air bubble, but you don't get the air if it's not lit.
     */
    public static boolean canProjectAirBubble(BlockState bs) {
        return (bs.is(Blocks.SOUL_CAMPFIRE) && bs.getValue(CampfireBlock.LIT))
            || bs.is(Blocks.SOUL_FIRE)
            || bs.is(Blocks.SOUL_TORCH) || bs.is(Blocks.SOUL_WALL_TORCH)
            || bs.is(Blocks.LAVA);
    }

    @Nullable
    public static Pair<AirQualityLevel, ForgeConfigSpec.DoubleValue> getPlaceBubble(BlockState bs) {
        if (bs.is(Blocks.SOUL_CAMPFIRE)) {
            return new Pair<>(AirQualityLevel.BLUE, MinersLungConfig.soulCampfireRange);
        } else if (bs.is(Blocks.SOUL_FIRE)) {
            return new Pair<>(AirQualityLevel.BLUE, MinersLungConfig.soulFireRange);
        } else if (bs.is(Blocks.SOUL_TORCH) || bs.is(Blocks.SOUL_WALL_TORCH)) {
            return new Pair<>(AirQualityLevel.BLUE, MinersLungConfig.soulTorchRange);
        } else if (bs.is(Blocks.LAVA)) {
            return new Pair<>(AirQualityLevel.RED, MinersLungConfig.lavaRange);
        } else {
            return null;
        }
    }

    public static Map<BlockPos, CapAirBubblePositions.Entry> recalcChunk(LevelChunk chunk) {
        var now = System.currentTimeMillis();

        var out = new HashMap<BlockPos, CapAirBubblePositions.Entry>();

        var minY = chunk.getMinBuildHeight();
        var cornerX = chunk.getPos().getMinBlockX();
        var cornerZ = chunk.getPos().getMinBlockZ();
        for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
                var x = cornerX + dx;
                var z = cornerZ + dz;
                for (int y = minY; y < chunk.getLevel().getHeight(Heightmap.Types.WORLD_SURFACE, x, z); y++) {
                    var pos = new BlockPos(x, y, z);
                    var bs = chunk.getBlockState(pos);
                    var bubble = getPlaceBubble(bs);
                    if (bubble != null) {
                        out.put(pos, new CapAirBubblePositions.Entry(bubble.getFirst(), bubble.getSecond().get()));
                    }
                }
            }
        }

        MinersLungMod.LOGGER.info("Freshly scanned chunk {}, took {} ms", chunk.getPos(),
            System.currentTimeMillis() - now);
        return out;
    }
}
