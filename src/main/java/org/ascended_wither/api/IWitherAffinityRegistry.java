package org.ascended_wither.api;

import java.util.Collection;

import net.minecraft.world.item.Item;

public interface IWitherAffinityRegistry
{

   boolean register(Item item);

   Collection<Item> getAll();

   boolean isRegistered(Item item);
}
