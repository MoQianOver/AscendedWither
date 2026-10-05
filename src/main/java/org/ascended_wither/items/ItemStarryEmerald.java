package org.ascended_wither.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemStarryEmerald extends Item implements IItem
{
   public ItemStarryEmerald()
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
