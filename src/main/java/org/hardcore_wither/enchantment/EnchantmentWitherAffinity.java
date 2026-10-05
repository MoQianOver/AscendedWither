package org.hardcore_wither.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class EnchantmentWitherAffinity extends Enchantment
{
   public static final String NAME = "wither_affinity";

   public EnchantmentWitherAffinity()
   {
      super(Rarity.VERY_RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
   }

   @Override
   public boolean canApplyAtEnchantingTable(ItemStack stack)
   {
      return false;
   }

   @Override
   public boolean checkCompatibility(Enchantment enchant)
   {
      return false;
   }

   @Override
   public boolean isAllowedOnBooks()
   {
       return false;
   }
}
