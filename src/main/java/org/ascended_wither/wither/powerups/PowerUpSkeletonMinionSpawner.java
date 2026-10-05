package org.ascended_wither.wither.powerups;

import org.ascended_wither.AscendedWither;
import org.ascended_wither.api.IPowerUpEffect;
import org.ascended_wither.config.Configurable;
import org.ascended_wither.entity.EntityRegistry;

@Configurable
public class PowerUpSkeletonMinionSpawner extends AbstractPowerUpMinionSpawner
{
   private final static int DEFAULT_MAX_STRENGTH = 64;
   private final static int DEFAULT_MIN_LEVEL = 3;

   public PowerUpSkeletonMinionSpawner()
   {
      super(DEFAULT_MIN_LEVEL, DEFAULT_MAX_STRENGTH, () -> EntityRegistry.SKELETON_MINION.get());
      AscendedWither.CONFIG.register(this);
   }

   @Override
   public void updateWither(IPowerUpEffect effect)
   {
      if (effect.getWither().isPowered() && effect.getWither().getInvulnerableTicks() <= 0)
      {
         super.updateWither(effect);
      }
   }
}
