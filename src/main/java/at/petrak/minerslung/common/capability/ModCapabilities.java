package at.petrak.minerslung.common.capability;

import at.petrak.minerslung.MinersLungMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ModCapabilities {
    public static final Capability<CapIsProtectedFromBadAir> IS_PROTECTED_FROM_AIR = CapabilityManager.get(
        new CapabilityToken<>() {
        });
    public static final Capability<CapAirBubblePositions> AIR_BUBBLE_POSITIONS = CapabilityManager.get(
        new CapabilityToken<>() {
        });

    @SubscribeEvent
    public static void registerCaps(RegisterCapabilitiesEvent evt) {
        evt.register(CapIsProtectedFromBadAir.class);
        evt.register(CapAirBubblePositions.class);
    }

    @SubscribeEvent
    public static void attachEntityCaps(AttachCapabilitiesEvent<Entity> evt) {
        if (evt.getObject() instanceof LivingEntity) {
            evt.addCapability(new ResourceLocation(MinersLungMod.MOD_ID, CapIsProtectedFromBadAir.CAP_NAME),
                new CapIsProtectedFromBadAir(false));
        }
    }

    @SubscribeEvent
    public static void attachChunkCaps(AttachCapabilitiesEvent<LevelChunk> evt) {
        evt.addCapability(new ResourceLocation(MinersLungMod.MOD_ID, CapAirBubblePositions.CAP_NAME),
            new CapAirBubblePositions());
    }
}
