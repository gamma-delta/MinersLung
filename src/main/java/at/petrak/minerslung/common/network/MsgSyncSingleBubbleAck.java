package at.petrak.minerslung.common.network;

import at.petrak.minerslung.MinersLungMod;
import at.petrak.minerslung.common.breath.AirBubble;
import at.petrak.minerslung.common.breath.AirQualityLevel;
import at.petrak.minerslung.common.capability.ModCapabilities;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Send the client info about a single bubble appearing or disappearing,
 * because for some asinine reason the event doesn't trigger on the client.
 * <p>
 * The entry will be null on removal.
 */
public record MsgSyncSingleBubbleAck(BlockPos pos, @Nullable AirBubble entry) {
    public static MsgSyncSingleBubbleAck deserialize(ByteBuf buffer) {
        var buf = new FriendlyByteBuf(buffer);
        var pos = buf.readBlockPos();
        AirBubble entry = null;
        var isNotNull = buf.readBoolean();
        if (isNotNull) {
            var airQuality = AirQualityLevel.values()[buf.readByte()];
            var radius = buf.readDouble();
            entry = new AirBubble(airQuality, radius);
        }

        return new MsgSyncSingleBubbleAck(pos, entry);
    }

    public void serialize(ByteBuf buffer) {
        var buf = new FriendlyByteBuf(buffer);
        buf.writeBlockPos(this.pos);
        buf.writeBoolean(this.entry != null);
        if (this.entry != null) {
            buf.writeByte(((byte) this.entry.airQuality().ordinal()));
            buf.writeDouble(this.entry.radius());
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                var world = Minecraft.getInstance().level;
                var chunkpos = new ChunkPos(this.pos);
                var chunk = world.getChunkSource().getChunkNow(chunkpos.x, chunkpos.z);
                if (chunk != null) {
                    var maybeCap = chunk.getCapability(ModCapabilities.AIR_BUBBLE_POSITIONS);
                    maybeCap.ifPresent(cap -> {
                        if (this.entry != null) {
                            var clobbered = cap.entries.put(this.pos, this.entry);
                            if (clobbered != null) {
                                MinersLungMod.LOGGER.warn("(client) clobbered single entry {} at {}", clobbered,
                                    this.pos);
                            }
                        } else {
                            var prev = cap.entries.remove(this.pos);
                            if (prev == null) {
                                MinersLungMod.LOGGER.warn("(client) tried to remove blank entry at {}", this.pos);
                            }
                        }
                    });
                }
            }));
        ctx.get().setPacketHandled(true);
    }
}
