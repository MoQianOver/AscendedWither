package org.ascended_wither.wither.powerups;

import org.ascended_wither.AscendedWither;
import org.ascended_wither.api.IPowerUpEffect;
import org.ascended_wither.config.Configurable;
import org.ascended_wither.entity.EntityRegistry;

@Configurable
public class PowerUpGhastMinionSpawner extends AbstractPowerUpMinionSpawner
{
   private final static int DEFAULT_MAX_STRENGTH = 20;
   private final static int DEFAULT_MIN_LEVEL = 3;

   public PowerUpGhastMinionSpawner()
   {
      super(DEFAULT_MIN_LEVEL, DEFAULT_MAX_STRENGTH, () -> EntityRegistry.GHAST_MINION.get());
      AscendedWither.CONFIG.register(this);
   }

   @Override
   public void updateWither(IPowerUpEffect effect)
   {
      if (effect.getWither().getInvulnerableTicks() <= 0 && !effect.getWither().isPowered())
      {
         super.updateWither(effect);
      }
   }
}
