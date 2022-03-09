package at.petrak.minerslung.common.network;

import at.petrak.minerslung.MinersLungMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages {
    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(MinersLungMod.MOD_ID, "main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );

    public static SimpleChannel getNetwork() {
        return NETWORK;
    }

    public static void register() {
        int messageIdx = 0;

        NETWORK.registerMessage(messageIdx++, MsgSyncBubblesSyn.class, MsgSyncBubblesSyn::serialize,
            MsgSyncBubblesSyn::deserialize, MsgSyncBubblesSyn::handle);
        NETWORK.registerMessage(messageIdx++, MsgSyncBubblesAck.class, MsgSyncBubblesAck::serialize,
            MsgSyncBubblesAck::deserialize, MsgSyncBubblesAck::handle);
        NETWORK.registerMessage(messageIdx++, MsgSyncSingleBubbleAck.class, MsgSyncSingleBubbleAck::serialize,
            MsgSyncSingleBubbleAck::deserialize, MsgSyncSingleBubbleAck::handle);
    }
}
