package at.petrak.minerslung.mixin;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public class LivingEntityAir {
    // We do the air level modification on our own, so ignore the game's inbuilt check
    // so here we pretend it can always breathe underwater
    @Redirect(method = "baseTick", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/entity/LivingEntity;canBreatheUnderwater()Z"))
    public boolean modifyNeverLoseOxygen(LivingEntity self) {
        return true;
    }

    // We also regenerate air on our own, so ignore the game's inbuilt check...
    // The game checks if `getAirSupply` is less than `getMaxAirSupply`, so we mixin
    // to make `getMaxAirSupply` always return negative a gazillion instead so it
    // never restores any air.
    @Redirect(
        method = "baseTick",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getMaxAirSupply()I", ordinal = 0))
    public int getAirSupplyProxy(LivingEntity e) {
        return Integer.MIN_VALUE;
    }
}
