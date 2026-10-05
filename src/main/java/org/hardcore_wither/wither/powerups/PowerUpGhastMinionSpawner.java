package org.hardcore_wither.wither.powerups;

import org.hardcore_wither.HardcoreWither;
import org.hardcore_wither.api.IPowerUpEffect;
import org.hardcore_wither.config.Configurable;
import org.hardcore_wither.entity.EntityRegistry;

@Configurable
public class PowerUpGhastMinionSpawner extends AbstractPowerUpMinionSpawner
{
   private final static int DEFAULT_MAX_STRENGTH = 20;
   private final static int DEFAULT_MIN_LEVEL = 3;

   public PowerUpGhastMinionSpawner()
   {
      super(DEFAULT_MIN_LEVEL, DEFAULT_MAX_STRENGTH, () -> EntityRegistry.GHAST_MINION.get());
      HardcoreWither.CONFIG.register(this);
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
