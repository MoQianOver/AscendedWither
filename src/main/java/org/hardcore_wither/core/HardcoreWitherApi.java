package org.hardcore_wither.core;

import org.hardcore_wither.api.IHardcoreWitherApi;
import org.hardcore_wither.api.IWitherAffinityRegistry;

public class HardcoreWitherApi implements IHardcoreWitherApi
{
   private final PowerUpRegistry        powerUpRegistry;
   private final WitherAffinityRegistry witherAffinityRegistry = new WitherAffinityRegistry();

   public HardcoreWitherApi(PowerUpRegistry powerUpRegistry)
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
