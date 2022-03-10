package at.petrak.minerslung.mixin;

import at.petrak.minerslung.common.breath.AirBubbleTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public class ServerLevelBlockTracker {
    @Inject(method = "onBlockStateChange", at = @At("HEAD"))
    protected void onOnBlockStateChange(BlockPos pPos, BlockState pBlockState, BlockState pNewState, CallbackInfo ci) {
        AirBubbleTracker.onBlockChanged((Level) (Object) this, pPos, pBlockState, pNewState);
    }
}
