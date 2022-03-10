package at.petrak.minerslung.mixin;

import at.petrak.minerslung.common.breath.AirBubbleTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// this mixins to not ClientLevel but that's cause it doesn't override onBlockStateChange
@Mixin(Level.class)
public class ClientLevelBlockTracker {
    @Inject(method = "onBlockStateChange", at = @At("HEAD"))
    protected void onOnBlockStateChange(BlockPos pPos, BlockState pBlockState, BlockState pNewState, CallbackInfo ci) {
        AirBubbleTracker.onBlockChanged((Level) (Object) this, pPos, pBlockState, pNewState);
    }
}
