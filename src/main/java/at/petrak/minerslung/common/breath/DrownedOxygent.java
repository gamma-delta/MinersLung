package at.petrak.minerslung.common.breath;

import at.petrak.minerslung.MinersLungConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DrownedOxygent {
    @SubscribeEvent
    public static void onDrownedAttack(LivingDamageEvent evt) {
        if (evt.getSource().getEntity() instanceof Drowned && !evt.getSource().isProjectile()) {
            var target = evt.getEntity();
            if (target instanceof LivingEntity living) {
                living.setAirSupply(living.getAirSupply() - MinersLungConfig.drownedChoking.get());
            }
        }
    }
}
