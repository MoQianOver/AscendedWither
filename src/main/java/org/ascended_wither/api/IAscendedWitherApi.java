package org.ascended_wither.api;

import org.ascended_wither.core.PowerUpRegistry;

public interface IAscendedWitherApi
{
   PowerUpRegistry getPowerUpRegistry();

   IWitherAffinityRegistry getWitherAffinityRegistry();
}
