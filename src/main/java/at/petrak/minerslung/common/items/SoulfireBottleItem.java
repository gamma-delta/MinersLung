package at.petrak.minerslung.common.items;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SoulfireBottleItem extends Item {
    public SoulfireBottleItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        player.setAirSupply(player.getMaxAirSupply());
        var stack = player.getItemInHand(hand);
        stack.shrink(1);
        world.playSound(player, player, SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 1.0f, 1.3f);
        world.playSound(player, player, SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, SoundSource.PLAYERS, 1.0f, 0.9f);
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.success(stack);
    }
}
