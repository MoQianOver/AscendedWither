package org.ascended_wither.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemStarryStick extends Item implements IItem
{
   public ItemStarryStick()
   {
      super(new Item.Properties());
   }

   @Override
   public boolean isFoil(ItemStack stack)
   {
      return true;
   }

   @Override
   public boolean isEnabled()
   {
      return true;
   }
}
