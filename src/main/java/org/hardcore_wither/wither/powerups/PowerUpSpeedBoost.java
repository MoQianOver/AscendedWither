package org.hardcore_wither.wither.powerups;

import org.hardcore_wither.HardcoreWither;
import org.hardcore_wither.api.IPowerUpEffect;
import org.hardcore_wither.config.Config;
import org.hardcore_wither.config.Configurable;
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
      HardcoreWither.CONFIG.register(this);
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
