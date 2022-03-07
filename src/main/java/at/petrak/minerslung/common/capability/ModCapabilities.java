package at.petrak.minerslung.common.capability;

import at.petrak.minerslung.MinersLungMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ModCapabilities {
    public static final Capability<CapIsUsingBladder> IS_USING_BLADDER = CapabilityManager.get(
        new CapabilityToken<>() {
        });

    @SubscribeEvent
    public static void registerCaps(RegisterCapabilitiesEvent evt) {
        evt.register(CapIsUsingBladder.class);
    }

    @SubscribeEvent
    public static void attachCaps(AttachCapabilitiesEvent<Entity> evt) {
        if (evt.getObject() instanceof LivingEntity) {
            evt.addCapability(new ResourceLocation(MinersLungMod.MOD_ID, CapIsUsingBladder.CAP_NAME),
                new CapIsUsingBladder(false));
        }
    }
}
