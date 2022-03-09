package at.petrak.minerslung.common.network;

import at.petrak.minerslung.common.capability.CapAirBubblePositions;
import at.petrak.minerslung.common.capability.ModCapabilities;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record MsgSyncBubblesAck(CapAirBubblePositions cap) {
    public static MsgSyncBubblesAck deserialize(ByteBuf buffer) {
        var buf = new FriendlyByteBuf(buffer);
        var tag = buf.readAnySizeNbt();
        var out = new CapAirBubblePositions();
        out.deserializeNBT(tag);
        return new MsgSyncBubblesAck(out);
    }

    public void serialize(ByteBuf buffer) {
        var buf = new FriendlyByteBuf(buffer);
        buf.writeNbt(this.cap.serializeNBT());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                var world = Minecraft.getInstance().level;
                // Each packet handles only one chunk, so we see if there's anything at all,
                // clear it, and add things.
                if (!this.cap.entries.isEmpty()) {
                    var anyOldPos = this.cap.entries.keySet().iterator().next();
                    var chunkpos = new ChunkPos(anyOldPos);
                    var chunk = world.getChunkSource().getChunkNow(chunkpos.x, chunkpos.z);
                    if (chunk != null) {
                        var maybeCap = chunk.getCapability(ModCapabilities.AIR_BUBBLE_POSITIONS);
                        maybeCap.ifPresent(cap -> {
                            cap.entries = this.cap.entries;
                        });
                    }
                }
            }));
        ctx.get().setPacketHandled(true);
    }
}
