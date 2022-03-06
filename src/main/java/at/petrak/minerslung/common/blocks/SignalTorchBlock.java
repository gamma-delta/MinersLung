package at.petrak.minerslung.common.blocks;

import at.petrak.minerslung.MinersLungConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Random;

public class SignalTorchBlock extends TorchBlock {
    public SignalTorchBlock(Properties pProperties) {
        super(pProperties, ParticleTypes.FLAME);
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, Random random) {
        double x = pPos.getX() + 0.5D;
        double y = pPos.getY() + 0.7D;
        double z = pPos.getZ() + 0.5D;
        double dx = (random.nextDouble() - 0.5) * 0.05;
        double dy = random.nextDouble() * 0.1;
        double dz = (random.nextDouble() - 0.5) * 0.05;
        pLevel.addParticle(ParticleTypes.FIREWORK, x, y, z, dx, dy, dz);
        pLevel.addParticle(this.flameParticle, x, y, z, 0.0D, 0.0D, 0.0D);
    }

    @Override
    public Item asItem() {
        return Items.TORCH;
    }

    @SubscribeEvent
    public static void switchTorchType(PlayerInteractEvent.RightClickBlock evt) {
        var player = evt.getPlayer();
        if (!MinersLungConfig.enableSignalTorches.get() || evt.getHand() != InteractionHand.MAIN_HAND || player.isDiscrete()) {
            return;
        }
        var pos = evt.getHitVec().getBlockPos();
        var bs = player.level.getBlockState(pos);

        Block nextBlock = null;
        float pitch = 1f;
        if (bs.is(Blocks.TORCH)) {
            nextBlock = ModBlocks.SIGNAL_TORCH.get();
        } else if (bs.is(Blocks.WALL_TORCH)) {
            nextBlock = ModBlocks.WALL_SIGNAL_TORCH.get();
        } else if (bs.is(ModBlocks.SIGNAL_TORCH.get())) {
            nextBlock = Blocks.TORCH;
            pitch = 0.8f;
        } else if (bs.is(ModBlocks.WALL_SIGNAL_TORCH.get())) {
            nextBlock = Blocks.WALL_TORCH;
            pitch = 0.8f;
        }
        if (nextBlock != null) {
            var nextBs = nextBlock.defaultBlockState();
            if (bs.hasProperty(WallTorchBlock.FACING)) {
                nextBs = nextBs.setValue(WallTorchBlock.FACING, bs.getValue(WallTorchBlock.FACING));
            }
            player.level.setBlockAndUpdate(pos, nextBs);
            player.level.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1f, pitch);
            player.swing(evt.getHand());
            evt.setUseItem(Event.Result.DENY);
        }
    }
}
