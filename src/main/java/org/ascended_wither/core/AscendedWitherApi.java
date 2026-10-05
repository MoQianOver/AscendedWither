package org.ascended_wither.core;

import org.ascended_wither.api.IAscendedWitherApi;
import org.ascended_wither.api.IWitherAffinityRegistry;

public class AscendedWitherApi implements IAscendedWitherApi
{
   private final PowerUpRegistry        powerUpRegistry;
   private final WitherAffinityRegistry witherAffinityRegistry = new WitherAffinityRegistry();

   public AscendedWitherApi(PowerUpRegistry powerUpRegistry)
   {
      this.powerUpRegistry = powerUpRegistry;
   }

   @Override
   public PowerUpRegistry getPowerUpRegistry()
   {
      return powerUpRegistry;
   }

   @Override
   public IWitherAffinityRegistry getWitherAffinityRegistry()
   {
      return witherAffinityRegistry;
   }
}
