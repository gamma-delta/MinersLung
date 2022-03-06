package at.petrak.minerslung.mixin;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public class LivingEntityAir {
    // We do the air level modification on our own, so ignore the game's inbuilt check
    // this is line 350 `flag1` in parchment mappings
    @ModifyVariable(method = "baseTick", at = @At("LOAD"), index = 2)
    public boolean modifyNeverLoseOxygen(boolean _isUnderwater) {
        return false;
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
