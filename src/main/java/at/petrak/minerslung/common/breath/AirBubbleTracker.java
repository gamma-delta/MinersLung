package at.petrak.minerslung.common.breath;

import at.petrak.minerslung.MinersLungConfig;
import at.petrak.minerslung.MinersLungMod;
import at.petrak.minerslung.common.capability.ModCapabilities;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AirBubbleTracker {
    public static void onBlockChanged(Level world, BlockPos pos, BlockState old, BlockState now) {
        var chunkPos = new ChunkPos(pos);
        var chunk = world.getChunkSource().getChunkNow(chunkPos.x, chunkPos.z);
        if (chunk != null) {
            var maybeCap = chunk.getCapability(ModCapabilities.AIR_BUBBLE_POSITIONS);
            maybeCap.ifPresent(cap -> {
                if (getPlaceBubble(old) != null) {
                    // need to remove this
                    var removed = cap.entries.remove(pos);
                    if (removed == null) {
                        MinersLungMod.LOGGER.warn("Didn't remove any air bubbles at {}", pos);
                    }
                    chunk.setUnsaved(true);
                }

                var bubble = getPlaceBubble(now);
                if (bubble != null && bubble.getSecond().get() != 0.0) {
                    var entry = new AirBubble(bubble.getFirst(), bubble.getSecond().get());
                    var clobbered = cap.entries.put(pos, entry);
                    if (clobbered != null) {
                        MinersLungMod.LOGGER.warn("Clobbered air bubble at {}: {}", pos, clobbered);
                    }
                    chunk.setUnsaved(true);
                }
            });
        }
    }

    // Two lists because in a singleplayer world these will be on the same JVM
    private static final Set<ChunkPos> clientChunksToScan = new HashSet<>();
    private static final Set<ChunkPos> serverChunksToScan = new HashSet<>();

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load evt) {
        var world = evt.getWorld();
        var chunkpos = evt.getChunk().getPos();
        var chunk = world.getChunkSource().getChunkNow(chunkpos.x, chunkpos.z);
        if (chunk != null) {
            var maybeCap = chunk.getCapability(ModCapabilities.AIR_BUBBLE_POSITIONS);
            maybeCap.ifPresent(cap -> {
                if (cap.skipCountLeft <= 0) {
                    cap.skipCountLeft = 16;
                    var chunksToScan = world.isClientSide() ? clientChunksToScan : serverChunksToScan;
                    chunksToScan.add(chunkpos);
                } else {
                    MinersLungMod.LOGGER.debug("Skipping chunk {} ({} left)", chunkpos, cap.skipCountLeft);
                    cap.skipCountLeft--;
                }
                chunk.setUnsaved(true);
            });
        }
    }

    @SubscribeEvent
    public static void consumeReqdChunksClient(TickEvent.ClientTickEvent evt) {
        var selection = clientChunksToScan.stream().limit(4).toList();
        for (var chunkpos : selection) {
            var chunk = Minecraft.getInstance().level.getChunkSource().getChunkNow(chunkpos.x, chunkpos.z);
            if (chunk != null) {
                var maybeCap = chunk.getCapability(ModCapabilities.AIR_BUBBLE_POSITIONS).resolve();
                if (maybeCap.isPresent()) {
                    var cap = maybeCap.get();
                    recalcChunk(chunk, cap.entries, true);
                    chunk.setUnsaved(true);
                }
            }
            clientChunksToScan.remove(chunkpos);
        }
    }

    @SubscribeEvent
    public static void consumeReqdChunksServer(TickEvent.WorldTickEvent evt) {
        var selection = serverChunksToScan.stream().limit(4).toList();
        for (var chunkpos : selection) {
            var chunk = evt.world.getChunkSource().getChunkNow(chunkpos.x, chunkpos.z);
            if (chunk != null) {
                var maybeCap = chunk.getCapability(ModCapabilities.AIR_BUBBLE_POSITIONS).resolve();
                if (maybeCap.isPresent()) {
                    var cap = maybeCap.get();
                    recalcChunk(chunk, cap.entries, false);
                    chunk.setUnsaved(true);
                }
            }
            serverChunksToScan.remove(chunkpos);
        }
    }

    @SubscribeEvent
    public static void onWorldClose(WorldEvent.Unload evt) {
        (evt.getWorld().isClientSide() ? clientChunksToScan : serverChunksToScan).clear();
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
            || bs.is(Blocks.LAVA)
            || bs.is(Blocks.NETHER_PORTAL);
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
        } else if (bs.is(Blocks.NETHER_PORTAL)) {
            return new Pair<>(AirQualityLevel.GREEN, MinersLungConfig.netherPortalRange);
        } else {
            return null;
        }
    }

    public static void recalcChunk(LevelChunk chunk, Map<BlockPos, AirBubble> out,
        boolean isClientSide) {
        var now = System.currentTimeMillis();

        out.clear();

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
                        out.put(pos, new AirBubble(bubble.getFirst(), bubble.getSecond().get()));
                    }
                }
            }
        }

        MinersLungMod.LOGGER.debug("({}) Freshly scanned chunk {}, took {} ms",
            isClientSide ? "client" : "server",
            chunk.getPos(),
            System.currentTimeMillis() - now);
    }
}
