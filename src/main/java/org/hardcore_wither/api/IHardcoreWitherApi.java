package org.hardcore_wither.api;

import org.hardcore_wither.core.PowerUpRegistry;

public interface IHardcoreWitherApi
{
   PowerUpRegistry getPowerUpRegistry();

   IWitherAffinityRegistry getWitherAffinityRegistry();
}
