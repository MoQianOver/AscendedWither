package org.ascended_wither.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import net.minecraft.world.item.Item;
import org.ascended_wither.api.IWitherAffinityRegistry;

public class WitherAffinityRegistry implements IWitherAffinityRegistry
{
   private final Collection<Item> items = new HashSet<>();

   @Override
   public boolean register(Item item)
   {
      return item != null && items.add(item);
   }

   @Override
   public Collection<Item> getAll()
   {
      return Collections.unmodifiableCollection(items);
   }

   @Override
   public boolean isRegistered(Item item)
   {
      return items.contains(item);
   }
}
