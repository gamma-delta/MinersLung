package at.petrak.minerslung.common.items;

import at.petrak.minerslung.common.breath.AirHelper;
import at.petrak.minerslung.common.breath.OxygenLevel;
import at.petrak.minerslung.common.capability.ModCapabilities;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class AirBladderItem extends Item {
    // We have to cache the air quality on the item in order to get the animations
    // because getUseAnimation doesn't provide the player
    private static final String TAG_OXYGEN_LEVEL = "oxygenLevel";

    public AirBladderItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        var maybeCap = pPlayer.getCapability(ModCapabilities.IS_USING_BLADDER).resolve();
        maybeCap.ifPresent(cap -> cap.isUsingBladder = true);
        return ItemUtils.startUsingInstantly(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        var o2Level = AirHelper.getO2LevelFromLocation(player);
        stack.getOrCreateTag().putString(TAG_OXYGEN_LEVEL, o2Level.toString());

        ServerPlayer maybeSplayer = null;
        if (player instanceof ServerPlayer splayer) {
            maybeSplayer = splayer;
        }
        if (o2Level == OxygenLevel.GREEN) {
            // refill
            stack.hurt(-4, player.getRandom(), maybeSplayer);
        } else if (stack.getDamageValue() < stack.getMaxDamage()) {
            // damage and replenish air
            stack.hurt(1, player.getRandom(), maybeSplayer);
            if (player.getAirSupply() < player.getMaxAirSupply()) {
                player.setAirSupply(player.getAirSupply() + 1);
            }
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        stopUsing(pStack, pLivingEntity);
        return pStack;
    }

    @Override
    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity, int pTimeCharged) {
        stopUsing(pStack, pLivingEntity);
    }

    public static void stopUsing(ItemStack stack, LivingEntity user) {
        stack.removeTagKey(TAG_OXYGEN_LEVEL);
        var maybeCap = user.getCapability(ModCapabilities.IS_USING_BLADDER).resolve();
        maybeCap.ifPresent(cap -> cap.isUsingBladder = false);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        var out = UseAnim.NONE;
        if (pStack.getDamageValue() < pStack.getMaxDamage() && pStack.hasTag()) {
            var tag = pStack.getTag();
            if (tag.contains(TAG_OXYGEN_LEVEL, Tag.TAG_STRING)) {
                var o2LevelStr = tag.getString(TAG_OXYGEN_LEVEL);
                try {
                    var o2Level = OxygenLevel.valueOf(o2LevelStr);
                    out = (o2Level == OxygenLevel.GREEN) ? UseAnim.BOW : UseAnim.DRINK;
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
        return out;
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 9001;
    }
}
