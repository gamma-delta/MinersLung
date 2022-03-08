package at.petrak.minerslung.common.items;

import at.petrak.minerslung.MinersLungMod;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public class RespiratorItem extends ArmorItem {
    public RespiratorItem(Properties pProperties) {
        super(RESPIRATOR_MATERIAL, EquipmentSlot.HEAD, pProperties);
    }

    public static ArmorMaterial RESPIRATOR_MATERIAL = new ArmorMaterial() {
        @Override
        public int getDurabilityForSlot(EquipmentSlot pSlot) {
            return 64;
        }

        @Override
        public int getDefenseForSlot(EquipmentSlot pSlot) {
            return 1;
        }

        @Override
        public int getEnchantmentValue() {
            return 15;
        }

        @Override
        public SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_GENERIC;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(Items.CHARCOAL);
        }

        @Override
        public String getName() {
            return MinersLungMod.MOD_ID + ":respirator";
        }

        @Override
        public float getToughness() {
            return 0;
        }

        @Override
        public float getKnockbackResistance() {
            return 0;
        }
    };
}
