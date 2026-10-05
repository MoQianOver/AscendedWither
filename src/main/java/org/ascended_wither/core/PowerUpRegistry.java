package org.ascended_wither.core;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import net.minecraft.resources.ResourceLocation;
import org.ascended_wither.api.IPowerUp;

public class PowerUpRegistry
{
   private final Map<ResourceLocation, IPowerUp> registry = new LinkedHashMap<>();

   public boolean register(IPowerUp powerUp)
   {
      ResourceLocation name = powerUp.getRegistryName();
      if (name == null || registry.containsKey(name))
      {
         return false;
      }
      registry.put(name, powerUp);
      return true;
   }

   public IPowerUp getValue(ResourceLocation name)
   {
      return registry.get(name);
   }

   public Set<ResourceLocation> getKeys()
   {
      return Collections.unmodifiableSet(registry.keySet());
   }

   public Collection<IPowerUp> getValues()
   {
      return Collections.unmodifiableCollection(registry.values());
   }

   public boolean containsKey(ResourceLocation name)
   {
      return registry.containsKey(name);
   }
}
