package at.petrak.minerslung.common.advancement;

import net.minecraft.advancements.CriteriaTriggers;

public class ModAdvancementTriggers {
    public static final BreatheAirTrigger BREATHE_AIR = new BreatheAirTrigger();
    public static final SignalificateTorchTrigger SIGNALIFICATE_TORCH = new SignalificateTorchTrigger();
    public static final UseSoulfireBecauseItDoesntTriggerVanillaForSomeReasonTrigger
        USE_SOULFIRE_BOTTLE = new UseSoulfireBecauseItDoesntTriggerVanillaForSomeReasonTrigger();

    public static void registerTriggers() {
        CriteriaTriggers.register(BREATHE_AIR);
        CriteriaTriggers.register(SIGNALIFICATE_TORCH);
        CriteriaTriggers.register(USE_SOULFIRE_BOTTLE);
    }
}
