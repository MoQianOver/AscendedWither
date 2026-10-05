package org.ascended_wither.wither.powerups;

import org.ascended_wither.AscendedWither;
import org.ascended_wither.api.IPowerUpEffect;
import org.ascended_wither.config.Config;
import org.ascended_wither.config.Configurable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.boss.wither.WitherBoss;

@Configurable
public class PowerUpSpeedBoost extends BasePowerUp
{
   private final static int DEFAULT_MAX_STRENGTH = 6;
   private final static int DEFAULT_MIN_LEVEL = 3;

   @Config(comment="I'd really reccomend 1")
   private static int speedBoostMultiplier = 1;

   protected PowerUpSpeedBoost()
   {
      super(DEFAULT_MIN_LEVEL, DEFAULT_MAX_STRENGTH);
      AscendedWither.CONFIG.register(this);
   }

   @Override
   public void updateWither(IPowerUpEffect data)
   {}

   @Override
   public void witherDied(IPowerUpEffect data)
   {}

   @Override
   public IPowerUpEffect getPowerUpEffect(WitherBoss wither, int strength)
   {
      return null;
   }

   @Override
   public IPowerUpEffect restorePowerUp(WitherBoss wither, CompoundTag stateNbt)
   {
      return null;
   }
}
