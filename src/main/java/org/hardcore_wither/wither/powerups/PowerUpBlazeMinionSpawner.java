package org.hardcore_wither.wither.powerups;

import org.hardcore_wither.HardcoreWither;
import org.hardcore_wither.api.IPowerUpEffect;
import org.hardcore_wither.config.Configurable;
import org.hardcore_wither.entity.EntityRegistry;

@Configurable
public class PowerUpBlazeMinionSpawner extends AbstractPowerUpMinionSpawner
{
   private final static int DEFAULT_MAX_STRENGTH = 20;
   private final static int DEFAULT_MIN_LEVEL = 2;

   public PowerUpBlazeMinionSpawner()
   {
      super(DEFAULT_MIN_LEVEL, DEFAULT_MAX_STRENGTH, () -> EntityRegistry.BLAZE_MINION.get());
      HardcoreWither.CONFIG.register(this);
   }

   @Override
   public void updateWither(IPowerUpEffect effect)
   {
      if (effect.getWither().getInvulnerableTicks() > 0)
      {
         super.updateWither(effect);
      }
   }
}
