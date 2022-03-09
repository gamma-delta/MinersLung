package at.petrak.minerslung.common.network;

import at.petrak.minerslung.common.capability.ModCapabilities;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public record MsgSyncBubblesSyn(int chunkX, int chunkZ) {
    public static MsgSyncBubblesSyn deserialize(ByteBuf buffer) {
        var buf = new FriendlyByteBuf(buffer);
        int x = buf.readInt();
        int z = buf.readInt();
        return new MsgSyncBubblesSyn(x, z);
    }

    public void serialize(ByteBuf buffer) {
        var buf = new FriendlyByteBuf(buffer);
        buf.writeInt(this.chunkX);
        buf.writeInt(this.chunkZ);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            var sender = ctx.get().getSender();
            if (sender != null) {
                var chunk = sender.level.getChunkSource().getChunkNow(this.chunkX, this.chunkZ);
                if (chunk != null) {
                    var maybeCap = chunk.getCapability(ModCapabilities.AIR_BUBBLE_POSITIONS);
                    maybeCap.ifPresent(cap -> {
                        ModMessages.getNetwork()
                            .send(PacketDistributor.PLAYER.with(() -> sender), new MsgSyncBubblesAck(cap));
                    });
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
